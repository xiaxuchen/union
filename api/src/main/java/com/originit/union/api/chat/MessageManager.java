package com.originit.union.api.chat;

import com.originit.union.api.chat.data.ChatUser;
import com.originit.union.api.chat.data.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 客服消息管理
 * @author xxc、
 */
@Component
public class MessageManager {

    private ConcurrentSkipListMap<String, List<Message>> userMessages;

    private ChatUserManager userManager;

    @Autowired
    public void setUserManager(ChatUserManager userManager) {
        this.userManager = userManager;
    }

    ReentrantLock lock = new ReentrantLock();

    public MessageManager() {
        this.userMessages = new ConcurrentSkipListMap<>();
    }

    private List<Message> createList () {
        return Collections.synchronizedList(new ArrayList<>());
    }

    /**
     * 在用户与客户经理之间发消息
     * @param openId 消息所属的用户id
     * @param message 消息
     */
    public void sendMessage (String openId, Message message) {
        lock.lock();
        try {
            // 检查用户是否存在列表中
            userManager.checkUser(openId);
            if (userMessages.get(openId) != null) {
                synchronized (userMessages.get(openId)) {
                    List<Message> messages = userMessages.get(openId);
                    messages.add(message);
                }
            } else {
                List<Message> list = createList();
                list.add(message);
                userMessages.put(openId,list);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取某个用户的最新的消息
     * @param openId 用户openId
     * @param count 消息条数
     * @return 用户的消息列表
     */
    public List<Message> getUserMessages(String openId,int count) {
        lock.lock();
        try {
            List<Message> messages = userMessages.get(openId);
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
}
