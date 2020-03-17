package com.originit.union.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.originit.union.chat.data.ChatUser;
import com.originit.union.entity.MessageEntity;

import java.util.List;

/**
 * 消息服务
 * @author xxc、
 */
public interface MessageService extends IService<MessageEntity> {

    /**
     * 获取所有
     */
    int ALL = -1;
    /**
     * 不要
     */
    int NONE = 0;

    /**
     * 用户发送消息，具体被谁接受由会话控制，将消息持久化
     * @param messageEntity 消息内容
     * @return 消息的id
     */
    Long sendMessage (MessageEntity messageEntity);

    /**
     * 分页获取等待的用户
     * @param curPage 当前页，从1开始
     * @param pageSize 每页的size
     * @param messageCount 需要的消息数量
     * @return 当前等待的用户以及用户的所有信息
     */
    List<ChatUser> getWaitingUsers(int curPage,int pageSize,int messageCount);


    /**
     * 接受一个用户
     * @param openId 用户的openId
     * @param id 经理的id
     */
    void receiveUser(String openId, Long id);

    /**
     * 关闭一个用户的连接
     * @param openId 用户的openId
     * @param id 经理的id
     */
    void disConnectUser(String openId, Long id);

    /**
     * 从一个客户经理转接到另一个客户经理
     * @param openId 用户的id
     * @param from 当前客户经理
     * @param to 转接的客户经理
     */
    void dispatchToOther(String openId, Long from, Long to);

    /**
     * 获取用户信息
     * @param openId 用户id
     * @param messageCount 用户消息数
     * @return 用户信息
     */
    ChatUser getUser(String openId, int messageCount);


    /**
     * 获取当前用户的状态
     * @param openId 用户id
     * @return 用户的状态
     */
    Integer getUserStatus(String openId);

    /**
     * 转换状态
     * @param openId 用户openId
     * @param status 用户的状态
     */
    void changeStatus(String openId, Integer status);

    /**
     * 获取正在等待的人的总数
     * @return 当前等待的用户数
     */
    Long getWaitingCount();


    /**
     * 获取经理的所有用户
     * @param userId 经理id
     * @param count 所需的用户数量
     * @param messageCount 消息数量
     * @return
     */
    List<ChatUser> getUserList(Long userId, int count, int messageCount);

    /**
     * 将消息状态设置为已读
     * @param messageIds 消息的id列表
     * @param userId
     */
    void readMessage(List<Long> messageIds, String userId);
}
