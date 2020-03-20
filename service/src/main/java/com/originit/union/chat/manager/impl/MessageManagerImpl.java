package com.originit.union.chat.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.originit.common.exceptions.DataConflictException;
import com.originit.common.util.RedisCacheProvider;
import com.originit.union.annotation.LockKey;
import com.originit.union.bussiness.ClientServeBusiness;
import com.originit.union.chat.data.ChatUser;
import com.originit.union.chat.manager.MessageManager;
import com.originit.union.chat.manager.UserManager;
import com.originit.union.constant.ChatConstant;
import com.originit.union.constant.WeChatConstant;
import com.originit.union.entity.MessageEntity;
import com.originit.union.mapper.MessageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author xxc、
 */
@Component
@Primary
public class MessageManagerImpl implements MessageManager {

    private MessageDao messageDao;

    private RedisCacheProvider provider;

    private UserManager userManager;

    private ClientServeBusiness clientServeBusiness;

    @Autowired
    public void setClientServeBusiness(ClientServeBusiness clientServeBusiness) {
        this.clientServeBusiness = clientServeBusiness;
    }

    @Autowired
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    @Autowired
    public void setMessageDao(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    @Autowired
    public void setProvider(RedisCacheProvider provider) {
        this.provider = provider;
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
        lambda.eq(MessageEntity::getAgentId,agentId);
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
    @LockKey(ChatConstant.USER_LOCK)
    public void sendMessageToUser(String userId, Long agentId, MessageEntity messageEntity) {
        ChatUser user = userManager.getUser(userId);
        // 若用户的经理id和经理的id相同，则发送消息
        if (user != null && agentId.equals(user.getReceiveAgent())) {
            // 发送消息
            clientServeBusiness.sendMessage(userId,messageEntity.getType(),messageEntity.getContent());
            messageEntity.setState(MessageEntity.STATE.READ);
            // 持久化
            messageDao.insert(messageEntity);
            return;
        }
        throw new IllegalStateException("该用户未接入");
    }

    @Override
    @LockKey(ChatConstant.USER_LOCK)
    public void sendMessageForServe(String userId, MessageEntity messageEntity) {
        Integer userState = userManager.getUserState(userId);
        switch (userState) {
            case ChatUser.STATE.NEVER:{
                if (isStartServe(messageEntity.getContent())) {
                    // 开启服务，用户等待
                    userManager.changeState(userId,ChatUser.STATE.WAIT,null);
                    clientServeBusiness.sendWaitMessage(userId);
                }
                break;
            }
            // 以下两个状态一样处理
            case ChatUser.STATE.RECEIVED: { }
            case ChatUser.STATE.WAIT: {
                if (isEndServe(messageEntity.getContent()))
                {
                    // 将用户直接下线
                    userManager.changeState(userId,ChatUser.STATE.NEVER,null);
                } else {
                    // 持久化消息
                    messageDao.insert(messageEntity);
                }
                break;
            }
            default:{
                throw new IllegalStateException("用户状态异常");
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void messageRead(List<Long> messages, Long agentId) {
        if (messages.isEmpty()) {
            throw new IllegalArgumentException("消息不能为空");
        }
        // 循环更新整个列表为改客户经理已读
        messages.forEach(id -> messageDao.update(null,new UpdateWrapper<MessageEntity>().lambda()
                .set(MessageEntity::getState,MessageEntity.STATE.READ)
                .set(MessageEntity::getAgentId,agentId)
                .eq(MessageEntity::getId,id)));
    }

    /**
     * 是否开启服务
     * @param message 用户消息
     * @return 是否开启服务
     */
    private boolean isStartServe(String message) {
        return WeChatConstant.CLIENT_SERVE_START.equals(message);
    }

    /**
     * 是否关闭服务
     * @param message 用户消息
     * @return 是否关闭服务
     */
    private boolean isEndServe(String message) {
        return WeChatConstant.CLIENT_SERVE_END.equals(message);
    }
}
