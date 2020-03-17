package com.originit.union.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.originit.union.chat.ChatSessionManager;
import com.originit.union.chat.ChatUserManager;
import com.originit.union.chat.MessageManager;
import com.originit.union.chat.data.ChatUser;
import com.originit.union.chat.data.Message;
import com.originit.union.entity.MessageEntity;
import com.originit.union.entity.UserBindEntity;
import com.originit.union.mapper.MessageDao;
import com.originit.union.mapper.UserDao;
import com.originit.union.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * @author xxc、
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageDao,MessageEntity>  implements MessageService {

    private MessageManager messageManager;

    private ChatUserManager chatUserManager;

    private ChatSessionManager sessionManager;

    private UserDao userDao;

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    public void setMessageManager(MessageManager messageManager) {
        this.messageManager = messageManager;
    }

    @Autowired
    public void setChatUserManager(ChatUserManager chatUserManager) {
        this.chatUserManager = chatUserManager;
    }

    @Autowired
    public void setSessionManager(ChatSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }


    /**
     * 通过openId获取用户id
     * @param openId 用户的openId
     * @return 用户的id
     */
    private Long getUserIdByOpenId (String openId) {
        return userDao.selectOne(new QueryWrapper<UserBindEntity>().lambda()
                .select(UserBindEntity::getId).eq(UserBindEntity::getOpenId,openId)).getId();
    }

    @Override
    public Long sendMessage(MessageEntity messageEntity) {
        // 发送消息给用户
        if (!messageEntity.getFromUser()) {
            messageManager.sendMessageToUser(messageEntity);
            messageEntity.setState(MessageEntity.STATE.READ);
        }
        // 将消息数据保存至数据库
        this.save(messageEntity);
        messageManager.sendMessage(messageEntity.getUserId(),messageEntity);
        return messageEntity.getId();
    }

    @Override
    public List<ChatUser> getWaitingUsers(int curPage, int pageSize, int messageCount) {
        // 获取指定页的等待用户列表以及所有的信息
        return chatUserManager.getWaitingUsers(curPage,pageSize,-1);
    }

    @Override
    public void receiveUser(String openId, Long id) {
        sessionManager.receiveUser(openId,id);
    }

    @Override
    public void disConnectUser(String openId, Long id) {
        sessionManager.disConnectUser(openId,id);
    }

    @Override
    public void dispatchToOther(String openId, Long from, Long to) {
        sessionManager.dispatchToOther(openId,from,to);
    }

    @Override
    public ChatUser getUser(String openId, int messageCount) {
        return chatUserManager.getUser(openId,messageCount);
    }

    @Override
    public Integer getUserStatus(String openId) {
        return chatUserManager.getUserStatus(openId);
    }

    @Override
    public void changeStatus(String openId, Integer status) {

    }

    @Override
    public Long getWaitingCount() {
        return (long) chatUserManager.getWaitingCount();
    }

    @Override
    public List<ChatUser> getUserList(Long userId, int count, int messageCount) {
        return sessionManager.getUserList(userId,count,messageCount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void readMessage(List<Long> messageIds, String userId) {
        messageIds.forEach(id -> {
            baseMapper.update(null,
                    new UpdateWrapper<MessageEntity>().lambda()
                            .set(MessageEntity::getState,MessageEntity.STATE.READ)
                            .eq(MessageEntity::getId,id));
        });
        messageManager.readMessages(messageIds,userId);
    }


}
