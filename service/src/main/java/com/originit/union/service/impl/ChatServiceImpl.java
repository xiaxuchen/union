package com.originit.union.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.originit.common.exceptions.DataConflictException;
import com.originit.common.exceptions.DataNotFoundException;
import com.originit.common.page.Pager;
import com.originit.union.bussiness.ClientServeBusiness;
import com.originit.union.chat.data.ChatUser;
import com.originit.union.dao.AgentStateDao;
import com.originit.union.dao.ChatUserAgentDao;
import com.originit.union.dao.ChatUserDao;
import com.originit.union.entity.ChatUserAgentEntity;
import com.originit.union.entity.ChatUserEntity;
import com.originit.union.entity.MessageEntity;
import com.originit.union.entity.converter.ChatConverter;
import com.originit.union.entity.vo.ChatMessageVO;
import com.originit.union.entity.vo.ChatUserVO;
import com.originit.union.dao.MessageDao;
import com.originit.union.service.ChatService;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

public class ChatServiceImpl implements ChatService {

    private MessageDao messageDao;

    private ChatUserAgentDao chatUserAgentDao;

    private ChatUserDao chatUserDao;

    private AgentStateDao agentStateDao;

    private ClientServeBusiness clientServeBusiness;

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
            throw new DataConflictException("当前用户已离线");
        }
        // 2. 确定当前经理连接了该用户
        final int findRelationCount = chatUserAgentDao.selectCount(new QueryWrapper<ChatUserAgentEntity>()
                .lambda().eq(ChatUserAgentEntity::getUserId,message.getUserId())
                .eq(ChatUserAgentEntity::getOpenId,message.getOpenId()));
        if (findRelationCount == 0) {
            throw new DataConflictException("当前用户已被其他经理接入");
        }
        // 3. 发送消息给微信用户，但是可能因为网络延迟而较晚收到
        clientServeBusiness.sendMessage(message.getOpenId(),message.getType(),message.getContent());
        // 发送完毕就认为已读
        message.setState(MessageEntity.STATE.READ);
        // 4. 持久化到数据库
        messageDao.insert(message);
        return message.getMessageId();
    }

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
        final int findUserCount = chatUserDao.selectCount(new QueryWrapper<ChatUserEntity>()
                .lambda().eq(ChatUserEntity::getOpenId, messageEntity.getOpenId()));
        if (findUserCount == 0) {
            // 2.1不在聊天就进入排队
            chatUserDao.insert(ChatUserEntity.builder()
                    .openId(messageEntity.getOpenId())
                    .build());
        }
        // 2.2 持久化消息
        messageDao.insert(messageEntity);
        return messageEntity.getMessageId();
    }

    @Override
    @Transactional(readOnly = true)
    public Pager<ChatUserVO> getWaitingUsers(int curPage, int pageSize) {
        // TODO 这里进行了多次查询，后期希望能成为一次
        // 1. 分页查询所有的用户的，时间逆序，后面来的在前面
        final IPage<ChatUserEntity> chatUserPage = chatUserDao.selectPage(new Page<>(curPage, pageSize), new QueryWrapper<ChatUserEntity>().lambda()
                .eq(ChatUserEntity::getState, ChatUserEntity.STATE.WAIT)
                .orderByDesc(ChatUserEntity::getGmtCreate));
        final List<ChatUserVO> records = chatUserPage.getRecords().stream().map(chatUserEntity -> {
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
        return new Pager<>(records,chatUserPage.getTotal());
    }

    @Override
    public void receiveUser(String openId, Long id) {

    }

    @Override
    public void disconnectUser(String openId, Long id) {

    }

    @Override
    public void dispatchToOther(String openId, Long from, Long to) {

    }

    @Override
    public ChatUser getUser(String openId, int messageCount) {
        return null;
    }

    @Override
    public Integer getUserStatus(String openId) {
        return null;
    }

    @Override
    public void readMessage(List<Long> messageIds, Long agentId) {

    }

    @Override
    public List<MessageEntity> getHistoryMessages(String userId, Long agentId, Long messageId) {
        return null;
    }

    @Override
    public List<MessageEntity> getWaitMessages(String userId, Long agentId, Long messageId) {
        return null;
    }

    @Override
    public List<ChatUserVO> getAgentUserVOs(Long agentId) {
        return null;
    }
}
