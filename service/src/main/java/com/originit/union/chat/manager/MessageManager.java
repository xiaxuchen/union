package com.originit.union.chat.manager;

import com.originit.union.entity.MessageEntity;

import java.util.List;

/**
 * 消息管理者，管理客服服务中的消息
 * @author xxc、
 */
public interface MessageManager {

    /**
     * 获取用户指定消息前的10条历史记录
     * @param userId 用户id
     * @param messageId 获取的历史记录最后一条的后面一条的id【(1，2，3，4，5)，6】，
     *                  假如消息是如此时间顺序存储，通过id 6可以获取到前面的若干条数据，
     *                  若messageId为null,则获取最新的
     * @param agentId 经理的id
     * @return messageId前的最多10条数据
     */
    List<MessageEntity> getHistoryMessages (String userId,Long agentId,Long messageId);

    /**
     * 获取用户等待读取的最新消息
     * @param userId 用户id
     * @param messageId 指定起始的消息的id，若没有则从最近未读的获取
     *                  如果最近未读也是空的，则获取最新的10条记录
     * @param agentId 经理的id
     * @return 用户指定消息后的未读的最多10条消息
     */
    List<MessageEntity> getWaitMessages (String userId,Long agentId,Long messageId);

    /**
     * 客户经理发送消息给用户
     * @param userId 用户的id
     * @param agentId 经理的id
     * @param messageEntity 消息
     */
    void sendMessageToUser (String userId,Long agentId,MessageEntity messageEntity);

    /**
     * 用户发送消息以获取客服服务
     * @param userId 用户id
     * @param messageEntity 消息实体
     */
    void sendMessageForServe (String userId, MessageEntity messageEntity);

    /**
     * 将某条消息设置为已读
     * @param messages 消息id
     * @param agentId
     */
    void messageRead(List<Long> messages, Long agentId);

}
