package com.originit.union.api.chat;

import com.originit.union.api.chat.data.ChatUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * 会话管理
 * @author xxc、
 */
@Component
@Primary
public class ChatSessionManager {

    private ChatUserManager chatUserManager;

    private ReentrantLock lock = new ReentrantLock();

    /**
     * 客户经理对应的用户
     */
    private ConcurrentSkipListMap<Long, List<String>> sessions;

    @Autowired
    public void setChatUserManager(ChatUserManager chatUserManager) {
        this.chatUserManager = chatUserManager;
    }

    public ChatSessionManager() {
        sessions = new ConcurrentSkipListMap<>();
    }

    /**
     * 获取用户信息以及消息列表
     * @param messageCount 每个用户获取的消息条数
     * @return 用户信息以及消息列表
     */
    public List<ChatUser> getUserList (Long id, int messageCount) {
        lock.lock();
        try {
            List<String> users = sessions.get(id);
            if (users == null || users.isEmpty()) {
                return Collections.emptyList();
            }
            return users.stream().map(s -> chatUserManager.getUser(s,messageCount)).collect(Collectors.toList());
        } finally {
            lock.unlock();
        }
    }

    /**
     * 接受一个用户
     * @param openId 用户的openId
     * @param id 经理的id
     */
    public void receiveUser (String openId,Long id) {
        lock.lock();
        try {
            chatUserManager.changeStatus(openId, ChatUser.STATE.RECEIVED);
            List<String> users = sessions.get(id);
            if (users == null) {
                users = new ArrayList<>();
            }
            users.add(openId);
            sessions.put(id,users);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 关闭一个用户的连接
     * @param openId 用户的openId
     * @param id 经理的id
     */
    public void disConnectUser (String openId,Long id) {
        lock.lock();
        try {
            chatUserManager.changeStatus(openId, ChatUser.STATE.NEVER);
            List<String> users = sessions.get(id);
            // 从中删除指定用户
            if (users != null) {
                users.removeIf(s -> s.equals(openId));
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 从一个客户经理转接到另一个客户经理
     * @param openId 用户的id
     * @param from 当前客户经理
     * @param to 转接的客户经理
     */
    public void dispatchToOther (String openId,Long from,Long to) {
        lock.lock();
        try {
            disConnectUser(openId,from);
            receiveUser(openId,to);
        } finally {
            lock.unlock();
        }
    }
}
