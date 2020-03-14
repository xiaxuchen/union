package com.originit.union.api.chat;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.originit.union.api.chat.data.ChatUser;
import com.originit.union.entity.UserBindEntity;
import com.originit.union.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * @author xxc、
 */
@Component
public class ChatUserManager {

    private ConcurrentSkipListMap<String, ChatUser> waitUsers;

    private ConcurrentSkipListMap<String, ChatUser> respondUsers;

    private ReentrantLock lock = new ReentrantLock();

    private UserService userService;

    private MessageManager messageManager;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setMessageManager(MessageManager messageManager) {
        this.messageManager = messageManager;
    }

    public ChatUserManager() {
        waitUsers = new ConcurrentSkipListMap<>();
        respondUsers = new ConcurrentSkipListMap<>();
    }

    /**
     * 检查是否存在该用户，不存在则添加到等待列表
     * @param openId 用户的openId
     */
    public void checkUser (String openId) {
        lock.lock();
        try {
            /**
             * 如果当前不存在该用户则创建
             */
            if (getUserStatus(openId) == ChatUser.STATE.NEVER) {
                // 添加到等待队列中
                waitUsers.put(openId,createNewUser(openId));
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取用户信息
     * @param openId 用户id
     * @param messageCount 用户消息数
     * @return 用户信息
     */
    public ChatUser getUser (String openId,int messageCount) {
        lock.lock();
        try {
            ChatUser chatUser = null;
            switch (getUserStatus(openId)) {
                case ChatUser.STATE.WAIT: {
                    chatUser = waitUsers.get(openId);
                    break;
                }
                case ChatUser.STATE.RECEIVED: {
                    chatUser = respondUsers.get(openId);
                    break;
                }
                case ChatUser.STATE.NEVER: {
                    chatUser = loadUserRespond(openId);
                    break;
                }
                default: { }
            }
            if (chatUser != null) {
                chatUser.setMessageList(messageManager.getUserMessages(chatUser.getUserInfo().getOpenId(),messageCount));
            }
            return chatUser;
        } finally {
            lock.unlock();
        }
    }

    private ChatUser loadUserRespond(String openId) {
        return null;
    }

    /**
     * 通过openid去构造用户
     * @param openId 用户openId
     * @return 用户实体
     */
    private ChatUser createNewUser(String openId) {
        // 从数据库中获取用户信息
        UserBindEntity userInfo = userService.getUserInfoByOpenId(openId);
        ChatUser chatUser = new ChatUser();
        chatUser.setUserInfo(userInfo);
        return chatUser;
    }

    /**
     * 获取当前用户的状态
     * @param openId 用户id
     * @return 用户的状态
     */
    public Integer getUserStatus (String openId) {
        lock.lock();
        try {
            if (waitUsers.get(openId) != null) {
                return ChatUser.STATE.WAIT;
            } else if (respondUsers.get(openId) != null) {
                return ChatUser.STATE.RECEIVED;
            } else {
                return ChatUser.STATE.NEVER;
            }
        }finally {
            lock.unlock();
        }
    }

    /**
     * 转换状态
     * @param openId 用户openId
     * @param status 用户的状态
     */
    public void changeStatus (String openId,Integer status) {
        lock.lock();
        try {
            switch (getUserStatus(openId)) {
                case ChatUser.STATE.NEVER: {
                    if (status == ChatUser.STATE.RECEIVED) {
                        waitUsers.put(openId,createNewUser(openId));
                    } else if (status == ChatUser.STATE.WAIT) {
                        respondUsers.put(openId,createNewUser(openId));
                    }
                    break;
                }
                case ChatUser.STATE.RECEIVED: {
                    if (status == ChatUser.STATE.WAIT) {
                        waitUsers.put(openId,respondUsers.remove(openId));
                    } else if (status == ChatUser.STATE.NEVER) {
                        respondUsers.remove(openId);
                    }
                    break;
                }
                case ChatUser.STATE.WAIT: {
                    if (status == ChatUser.STATE.RECEIVED) {
                        respondUsers.put(openId,waitUsers.remove(openId));
                    } else if (status == ChatUser.STATE.NEVER) {
                        waitUsers.remove(openId);
                    }
                    break;
                }
                default:{
                    throw new IllegalStateException("用户的状态错误");
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取正在等待的用户列表
     * @param userCount 获取用户的数量
     * @param messageCount 获取信息列表的数量
     * @return 用户数据列表
     */
    public List<ChatUser> getWaitingUsers (int curPage,int userCount,int messageCount) {
        lock.lock();
        try {
            int size = waitUsers.size();
            if (size == 0) {
                return Collections.emptyList();
            }
            List<ChatUser> chatUsers = ChatUtil.subList(new ArrayList<>(waitUsers.values()), curPage, userCount);
            if (messageCount == 0) {
                return chatUsers;
            }
            return chatUsers.stream()
                    .peek(chatUser -> chatUser.setMessageList(messageManager.getUserMessages(chatUser.getUserInfo().getOpenId(), messageCount)))
                    .collect(Collectors.toList());
        } finally {
            lock.unlock();
        }
    }

    /**
     * 定期清除过期用户
     */
    public void clearUsers () {

    }

    /**
     * 获取等待总数
     * @return 等待总数
     */
    public int getWaitingCount() {
        return waitUsers.size();
    }
}
