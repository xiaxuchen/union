package com.originit.union.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.originit.common.exceptions.DataConflictException;
import com.originit.common.page.Pager;
import com.originit.union.bussiness.ClientServeBusiness;
import com.originit.union.chat.data.ChatUser;
import com.originit.union.constant.ChatConstant;
import com.originit.union.constant.WeChatConstant;
import com.originit.union.dao.*;
import com.originit.union.entity.*;
import com.originit.union.entity.converter.ChatConverter;
import com.originit.union.entity.vo.AgentIntroduceVO;
import com.originit.union.entity.vo.ChatUserVO;
import com.originit.union.exception.chat.ChatException;
import com.originit.union.exception.chat.ChatUserAlreadyReceiveException;
import com.originit.union.exception.chat.ChatUserOfflineException;
import com.originit.union.service.ChatService;
import com.originit.union.util.DataUtil;
import com.originit.union.websocket.ChatServer;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    private SimpMessagingTemplate messagingTemplate;

    private MessageDao messageDao;

    private ChatUserAgentDao chatUserAgentDao;

    private ChatUserDao chatUserDao;

    private AgentStateDao agentStateDao;

    private AgentInfoDao agentInfoDao;

    private SqlSessionFactory sqlSessionFactory;

    private ClientServeBusiness clientServeBusiness;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long sendMessageToUser(MessageEntity message) {
        // 校验消息的是否来自于客户经理
        if (message.getFromUser() == null) {
            message.setFromUser(false);
        } else if (message.getFromUser()) {
            throw new IllegalStateException("发送给用户的消息不应该来自于用户");
        }
        // 1. 查找发送的用户
        final int findUserCount = chatUserDao.selectCount(new QueryWrapper<ChatUserEntity>()
                .lambda().eq(ChatUserEntity::getState,ChatUser.STATE.RECEIVED)
                .eq(ChatUserEntity::getOpenId, message.getOpenId()));
        if (findUserCount == 0) {
            throw new ChatUserOfflineException();
        }
        // 2. 确定当前经理连接了该用户
        final int findRelationCount = chatUserAgentDao.selectCount(new QueryWrapper<ChatUserAgentEntity>()
                .lambda().eq(ChatUserAgentEntity::getUserId,message.getUserId())
                .eq(ChatUserAgentEntity::getOpenId,message.getOpenId()));
        if (findRelationCount == 0) {
            throw new ChatUserAlreadyReceiveException("当前用户已被其他经理接入");
        }
        // 3. 发送消息给微信用户，但是可能因为网络延迟而较晚收到
        clientServeBusiness.sendMessage(message.getOpenId(),message.getType(),message.getContent());
        // 发送完毕就认为已读
        message.setState(MessageEntity.STATE.READ);
        // 4. 持久化到数据库
        messageDao.insert(message);
        return message.getMessageId();
    }

    /**
     * 后期扩展的时候无论开始结束怎么变，
     * 只要在接受消息的地方转化为CLIENT_SERVE_START、CLIENT_SERVE_END
     * @param messageEntity 用户实体信息
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long sendMessageForServe(MessageEntity messageEntity) {
        // 校验消息的是否来自于用户
        if (messageEntity.getFromUser() == null) {
            // 如果未设置就设置为来自用户
            messageEntity.setFromUser(true);
        } else if (!messageEntity.getFromUser()) {
            throw new IllegalStateException("用户发送的消息应该来自于用户");
        }
        // 放置上层参数传递错误
        if (messageEntity.getState() == MessageEntity.STATE.READ) {
            throw new IllegalStateException("用户发送的消息不能初始为已读");
        }
        // 1. 判断用户是否已经在聊天
        final ChatUserEntity chatUser = chatUserDao.selectOne(new QueryWrapper<ChatUserEntity>()
                .lambda().select(ChatUserEntity::getState).eq(ChatUserEntity::getOpenId, messageEntity.getOpenId()));
        // 状态为空表明表中没有该用户，即没有要求客服服务
        if (chatUser == null) {
            // 2.1 如果是开始消息则加入排队,其他的不管
            if (WeChatConstant.CLIENT_SERVE_START.equals(messageEntity.getContent())) {
                chatUserDao.insert(ChatUserEntity.builder()
                        .openId(messageEntity.getOpenId())
                        .state(MessageEntity.STATE.WAIT)
                        .build());
                // 发送消息通知用户，已经在排队了
                clientServeBusiness.sendWaitMessage(messageEntity.getOpenId());
                // 发送用户数量更新
                messagingTemplate.convertAndSend(ChatConstant
                        .WS_WAIT_COUNT_UPDATE,DataUtil.mapBuilder()
                        .append("count",getWaitingUserCount())
                        .append("time",System.currentTimeMillis())
                        .build());
            }
            return null;
        }
        Integer state = chatUser.getState();
        // 如果已经在排队中了，且输入了退出排队指令，则退出排队，同时断开连接
        if (WeChatConstant.CLIENT_SERVE_END.equals(messageEntity.getContent())) {
            // 删除用户排队
            chatUserDao.delete(new QueryWrapper<ChatUserEntity>().lambda()
                    .eq(ChatUserEntity::getOpenId,messageEntity.getOpenId()));
            // 删除与之相关的聊天关系
            chatUserAgentDao.delete(new QueryWrapper<ChatUserAgentEntity>().lambda()
                    .eq(ChatUserAgentEntity::getOpenId,messageEntity.getOpenId()));
            // 发送通知给微信用户，服务断开
            clientServeBusiness.sendExitMessage(messageEntity.getOpenId());
            // 如果是等待状态，则通知经理等待用户的数量减少了
            if (state == ChatUserEntity.STATE.WAIT) {
                messagingTemplate.convertAndSend(ChatConstant
                        .WS_WAIT_COUNT_UPDATE,DataUtil.mapBuilder()
                        .append("count",getWaitingUserCount())
                        .append("time",System.currentTimeMillis())
                        .build());
            }
            return null;
        }
        // 2.2 持久化消息
        messageDao.insert(messageEntity);
        // 2.3 TODO 获取状态的时候同时获取所属的经理,通知客户经理有新的消息
        if (state == ChatUserEntity.STATE.RECEIVE) {
//            ChatServer.sendNewMessage(ChatConverter.INSTANCE.to(messageEntity),
//                    chatUserAgentDao.selectOne(new QueryWrapper<ChatUserAgentEntity>()
//                            .lambda().select(ChatUserAgentEntity::getUserId).eq(ChatUserAgentEntity::getOpenId,messageEntity.getOpenId()))
//                            .getUserId());
        }
        return messageEntity.getMessageId();
    }

    /**
     * 获取等待用户的数量
     * @return 等待用户的数量
     */
    private int getWaitingUserCount () {
        return chatUserDao.selectCount(new QueryWrapper<ChatUserEntity>().lambda()
                    .eq(ChatUserEntity::getState,ChatUserEntity.STATE.WAIT));
    }

    @Override
    @Transactional(readOnly = true)
    public Pager<ChatUserVO> getWaitingUsers(int curPage, int pageSize) {
        // TODO 这里进行了多次查询，后期希望能成为一次
        // 1. 分页查询所有的用户的，时间逆序，后面来的在前面
        final IPage<UserBindEntity> chatUserPage = chatUserDao.selectWaitingUsers(new Page<>(curPage, pageSize));
        // 将用户信息转换为vo，同时获取用户的最后信息
        final List<ChatUserVO> records = convertChatUserVOWithLastMessage(chatUserPage.getRecords());
        return new Pager<>(records,chatUserPage.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void receiveUser(String openId, Long id) {
        // 1. 更新该用户的状态为已接受
        int count = chatUserDao.update(null,new UpdateWrapper<ChatUserEntity>()
                .lambda().set(ChatUserEntity::getState,ChatUserEntity.STATE.RECEIVE)
        .eq(ChatUserEntity::getOpenId,openId));
        // 如果没有修改成功，就说明不存在或状态已经是RECEIVE
        if (count == 0) {
            throw new ChatUserAlreadyReceiveException("该用户已被其他经理接入或离线");
        }
        // 1.1将当前所有未读且没有经理归属的消息归属到连接的经理下
        messageDao.update(null,new UpdateWrapper<MessageEntity>()
                .lambda().set(MessageEntity::getUserId,id).isNull(MessageEntity::getUserId).eq(MessageEntity::getOpenId,openId)
                .eq(MessageEntity::getState,MessageEntity.STATE.WAIT));
        // 2.将经理和用户的关系插入数据库中
        chatUserAgentDao.insert(ChatUserAgentEntity.builder()
                .openId(openId)
                .userId(id)
                .build());
        // 3. 发送接入通知给聊天用户
        AgentIntroduceVO agentInfo = agentInfoDao.selectAgentInfo (id);
        clientServeBusiness.sendAgentIntroduce(openId,agentInfo.getName(),agentInfo.getDes(),agentInfo.getHeadImg());
        // 4. 通知所有的经理该用户已被接受，同时更新当前的数量
        messagingTemplate.convertAndSend(ChatConstant.WS_USER_RECEIVED,DataUtil.mapBuilder()
                .append("count",getWaitingUserCount())
                .append("openId",openId)
                .append("time",System.currentTimeMillis())
                .build());
    }

    @Override
    public void disconnectUser(String openId, Long id) {
        // 1. 从关系表中删除改微信用户和经理的关系
        final int deleted = chatUserAgentDao.delete(new QueryWrapper<ChatUserAgentEntity>()
                .lambda().eq(ChatUserAgentEntity::getUserId, id)
                .eq(ChatUserAgentEntity::getOpenId, openId)
                .eq(ChatUserAgentEntity::getUserId, id));
        if (deleted == 0) {
            throw new ChatUserAlreadyReceiveException("该用户没有被您接入");
        }
        // 2. 删除聊天用户表中的用户，当前用户处于离线
        chatUserDao.delete(new QueryWrapper<ChatUserEntity>().lambda().eq(ChatUserEntity::getOpenId,openId));
        // 3. 发送通知给微信用户，服务断开
        clientServeBusiness.sendExitMessage(openId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void dispatchToOther(String openId, Long from, Long to) {
        // 0. 校验经理的id
        if (from == null || to == null || from.equals(to)) {
            throw new IllegalArgumentException("经理的id不能为空也不能相同");
        }
        // 1. 检查当前用户是否处于已接入状态
        ChatUserEntity chatUser = chatUserDao.selectOne(new QueryWrapper<ChatUserEntity>()
                .lambda().select(ChatUserEntity::getState).eq(ChatUserEntity::getOpenId,openId));
        // 1.1 状态为空表示找不到该用户，即已离线
        if (chatUser == null) {
            throw new ChatUserOfflineException();
        }
        Integer state = chatUser.getState();
        // 1.2 如果状态为等待就改为接受
        if (state == ChatUserEntity.STATE.WAIT) {
            chatUserDao.update(null,new UpdateWrapper<ChatUserEntity>()
                    .lambda().set(ChatUserEntity::getState,ChatUserEntity.STATE.RECEIVE)
                    .eq(ChatUserEntity::getOpenId,openId));
            // 1.2.1 将用户插入到关系表中
            chatUserAgentDao.insert(ChatUserAgentEntity.builder()
                    .userId(to)
                    .openId(openId)
                    .build());
        } else if (state == ChatUser.STATE.RECEIVED) {
            // 1.3 如果用户是已接受，则只需要将原关系中的经理修改为当前经理
            int count = chatUserAgentDao.update(null,
                    new UpdateWrapper<ChatUserAgentEntity>()
            .lambda().set(ChatUserAgentEntity::getUserId,to)
            .eq(ChatUserAgentEntity::getOpenId,openId)
            .eq(ChatUserAgentEntity::getUserId,from));
            // 如果没修改则说明出现问题了，要么是两个经理id是一样，要么就是没有这条消息
            if (count == 0) {
                throw new ChatException("用户状态异常");
            }
        }
        // TODO 发送转接信息
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void readMessage(List<Long> messageIds, Long agentId) {
        try(SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false)) {
            final MessageDao batchMessageDao = sqlSession.getMapper(MessageDao.class);
            for (Long messageId : messageIds) {
                // 将消息的状态设置为已读同时读取的用户id设置为当前经理
                batchMessageDao.update(null,
                        new UpdateWrapper<MessageEntity>().lambda()
                                .set(MessageEntity::getState,MessageEntity.STATE.READ)
                                .set(MessageEntity::getUserId,agentId)
                        .eq(MessageEntity::getMessageId,messageId));
            }
            sqlSession.flushStatements();
            sqlSession.commit();
        }
    }

    @Override
    public List<MessageEntity> getHistoryMessages(String userId,Long agentId, Long messageId) {
        MessageEntity entity = null;
        if (messageId != null) {
            entity = messageDao.selectById(messageId);
            if (entity == null) {
                throw new DataConflictException("不存在此消息");
            }
        }
        LambdaQueryWrapper<MessageEntity> lambda = new QueryWrapper<MessageEntity>().lambda();
        // 如果当前指定最后的消息，则设置
        if (messageId != null) {
            lambda.lt(MessageEntity::getGmtCreate,entity.getGmtCreate());
        }
        // 要是这个客户经理的
        lambda.eq(MessageEntity::getUserId,agentId);
        lambda.orderByDesc(MessageEntity::getGmtCreate);
        // 获取最后10条
        return messageDao.selectPage(new Page<>(0, 10),lambda).getRecords();
    }

    @Override
    public List<MessageEntity> getWaitMessages(String userId,Long agentId, Long messageId) {
        MessageEntity entity = null;
        if (messageId != null) {
            entity = messageDao.selectById(messageId);
            if (entity == null) {
                throw new DataConflictException("不存在此消息");
            }
        }
        LambdaQueryWrapper<MessageEntity> lambda = new QueryWrapper<MessageEntity>().lambda();
        // 如果当前指定最后的消息，则设置
        if (messageId != null) {
            lambda.gt(MessageEntity::getGmtCreate,entity.getGmtCreate());
        }
        lambda.eq(MessageEntity::getState,MessageEntity.STATE.WAIT);
        // 顺序获取
        lambda.orderByAsc(MessageEntity::getGmtCreate);
        // 获取最后10条
        return messageDao.selectPage(new Page<>(0, 10),lambda).getRecords();
    }
    @Override
    @Transactional(readOnly = true)
    public List<ChatUserVO> getAgentUserVOs(Long agentId) {
        // 1. 分页查询所有的用户的，时间逆序，后面来的在前面
        final List<UserBindEntity> users = chatUserAgentDao.selectAgentUsers(agentId);
        // 2. 将用户信息转换为VO同时获取最后一条消息
        return convertChatUserVOWithLastMessage(users);
    }

    /**
     * 抽取的转换用户且获取用户最后一条消息的方法
     * @param users 用户信息
     * @return 用户显示对象（包括最后一条信息）
     */
    private List<ChatUserVO> convertChatUserVOWithLastMessage (List<UserBindEntity> users) {
        if (users == null || users.isEmpty()) {
            return Collections.emptyList();
        }
        final List<ChatUserVO> records = users.stream().map(chatUserEntity -> {
            // 查找该用户最新的一条消息
            final IPage<MessageEntity> messagePage = messageDao.selectPage(new Page<>(1, 1), new QueryWrapper<MessageEntity>().lambda()
                    .eq(MessageEntity::getOpenId, chatUserEntity.getOpenId())
                    .eq(MessageEntity::getState, MessageEntity.STATE.WAIT)
                    .orderByDesc(MessageEntity::getGmtCreate));
            final ChatUserVO chatUserVO = ChatConverter.INSTANCE.convertWaitingUser(chatUserEntity, (int) messagePage.getTotal());
            // 如果查询到了未读消息，则填入其中
            if (!messagePage.getRecords().isEmpty()) {
                chatUserVO.setLastMessage(ChatConverter.INSTANCE.to(messagePage.getRecords().get(0)));
            }
            return chatUserVO;
        }).collect(Collectors.toList());
        return records;
    }


    @Autowired
    public void setMessagingTemplate(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Autowired
    public void setAgentInfoDao(AgentInfoDao agentInfoDao) {
        this.agentInfoDao = agentInfoDao;
    }

    @Autowired
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    @Autowired
    public void setClientServeBusiness(ClientServeBusiness clientServeBusiness) {
        this.clientServeBusiness = clientServeBusiness;
    }

    @Autowired
    public void setChatUserAgentDao(ChatUserAgentDao chatUserAgentDao) {
        this.chatUserAgentDao = chatUserAgentDao;
    }

    @Autowired
    public void setChatUserDao(ChatUserDao chatUserDao) {
        this.chatUserDao = chatUserDao;
    }

    @Autowired
    public void setAgentStateDao(AgentStateDao agentStateDao) {
        this.agentStateDao = agentStateDao;
    }

    @Autowired
    public void setMessageDao(MessageDao messageDao) {
        this.messageDao = messageDao;
    }
}
