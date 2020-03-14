package com.originit.union.api.chat;

import com.originit.union.api.chat.data.ChatUser;
import com.originit.union.api.chat.data.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author xxc、
 */
@Component
public class ChatDoor {

    private MessageManager messageManager;

    private ChatUserManager chatUserManager;

    private ChatSessionManager sessionManager;

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
     * 获取用户信息以及消息列表
     * @param messageCount 每个用户获取的消息条数
     * @return 用户信息以及消息列表
     */
    public List<ChatUser> getUserList (Long id, int messageCount) {
        return sessionManager.getUserList(id,messageCount);
    }

    /**
     * 接受一个用户
     * @param openId 用户的openId
     * @param id 经理的id
     */
    public void receiveUser (String openId,Long id) {
        sessionManager.receiveUser(openId,id);
    }

    /**
     * 关闭一个用户的连接
     * @param openId 用户的openId
     * @param id 经理的id
     */
    public void disConnectUser (String openId,Long id) {
        sessionManager.disConnectUser(openId,id);
    }

    /**
     * 从一个客户经理转接到另一个客户经理
     * @param openId 用户的id
     * @param from 当前客户经理
     * @param to 转接的客户经理
     */
    public void dispatchToOther (String openId,Long from,Long to) {
        sessionManager.dispatchToOther(openId,from,to);
    }


    /**
     * 获取用户信息
     * @param openId 用户id
     * @param messageCount 用户消息数
     * @return 用户信息
     */
    public ChatUser getUser (String openId,int messageCount) {
        return chatUserManager.getUser(openId,messageCount);
    }


    /**
     * 获取当前用户的状态
     * @param openId 用户id
     * @return 用户的状态
     */
    public Integer getUserStatus (String openId) {
        return chatUserManager.getUserStatus(openId);
    }

    /**
     * 转换状态
     * @param openId 用户openId
     * @param status 用户的状态
     */
    public void changeStatus (String openId,Integer status) {
        chatUserManager.changeStatus(openId,status);
    }

    /**
     * 获取正在等待的用户列表
     * @param userCount 获取用户的数量
     * @param messageCount 获取信息列表的数量
     * @return 用户数据列表
     */
    public List<ChatUser> getWaitingUsers (int curPage,int userCount,int messageCount) {
        return chatUserManager.getWaitingUsers(curPage,userCount,messageCount);
    }

    /**
     * 获取正在等待的人的总数
     */
    public int getWaitingCount () {
        return chatUserManager.getWaitingCount();
    }

    /**
     * 在用户与客户经理之间发消息
     * @param openId 消息所属的用户id
     * @param message 消息
     */
    public void sendMessage (String openId, Message message) {
        messageManager.sendMessage(openId,message);
    }

    /**
     * 获取某个用户的最新的消息
     * @param openId 用户openId
     * @param count 消息条数
     * @return 用户的消息列表
     */
//    public List<Message> getUserMessages(String openId,int count) {
//        return messageManager.getUserMessages(openId, count);
//    }

}
