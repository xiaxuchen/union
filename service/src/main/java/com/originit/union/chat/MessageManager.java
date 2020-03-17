package com.originit.union.chat;

import com.originit.union.bussiness.ClientServeBusiness;
import com.originit.union.bussiness.MessageBusiness;
import com.originit.union.entity.MessageEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 管理当前处于等待或者接入中的用户的消息
 * @author xxc、
 */
@Component
public class MessageManager {

    private ClientServeBusiness clientServeBusiness;

    private ConcurrentSkipListMap<String, List<MessageEntity>> userMessages;

    private ChatUserManager userManager;

    @Autowired
    public void setClientServeBusiness(ClientServeBusiness messageBusiness) {
        this.clientServeBusiness = messageBusiness;
    }

    @Autowired
    public void setUserManager(ChatUserManager userManager) {
        this.userManager = userManager;
    }

    private ReentrantLock lock = new ReentrantLock();

    public MessageManager() {
        this.userMessages = new ConcurrentSkipListMap<>();
    }

    private List<MessageEntity> createList () {
        return Collections.synchronizedList(new ArrayList<>());
    }

    /**
     * 在用户与客户经理之间发消息
     * @param openId 消息所属的用户id
     * @param message 消息
     */
    public void sendMessage (String openId, MessageEntity message) {
        lock.lock();
        try {
            // 检查用户是否处于聊天状态或开启聊天
            if (!userManager.checkUser(openId, message.getContent())) {
                return;
            }
            if (userMessages.get(openId) != null) {
                synchronized (userMessages.get(openId)) {
                    List<MessageEntity> messages = userMessages.get(openId);
                    messages.add(message);
                }
            } else {
                List<MessageEntity> list = createList();
                list.add(message);
                userMessages.put(openId,list);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 发送消息给用户
     * @param message 消息
     */
    public void sendMessageToUser(MessageEntity message) {
        switch (message.getType()) {
            case MessageEntity.TYPE.TEXT: {
                clientServeBusiness.sendTextMessage(message.getUserId(),message.getContent());
                break;
            }
            case MessageEntity.TYPE.IMAGE: {
                break;
            }
            default:{}
        }
    }

    /**
     * 获取某个用户的最新的消息
     * @param openId 用户openId
     * @param count 消息条数
     * @return 用户的消息列表
     */
    public List<MessageEntity> getUserMessages(String openId,int count) {
        lock.lock();
        try {
            List<MessageEntity> messages = userMessages.get(openId);
            // 如果消息列表为空就返回空列表
            if (messages == null || messages.isEmpty()) {
                return Collections.emptyList();
            }
            int size = messages.size();
            return messages.subList(ChatUtil.getFromSize(count,size), size);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 已读取了信息
     * @param messageIds 信息id
     * @param userId 用户id
     */
    public void readMessages(List<Long> messageIds, String userId) {
        List<MessageEntity> messageEntities = userMessages.get(userId);
        messageEntities.forEach(messageEntity -> {
            if (messageIds.stream().anyMatch((id) -> id.equals(messageEntity.getId()))) {
                messageEntity.setState(MessageEntity.STATE.READ);
            }
        });
    }

    /**
     * 用户的消息清除
     * @param openId 用户id
     */
    public void clearMessage(String openId) {
        userMessages.remove(openId);
    }
}
