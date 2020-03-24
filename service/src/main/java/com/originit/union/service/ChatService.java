package com.originit.union.service;

import com.originit.common.page.Pager;
import com.originit.union.chat.data.ChatUser;
import com.originit.union.entity.MessageEntity;
import com.originit.union.entity.vo.ChatUserVO;

import java.util.List;

/**
 * 聊天服务
 * @author xxc、
 */
public interface ChatService {
    /**
     * 获取所有
     */
    int ALL = -1;
    /**
     * 不要
     */
    int NONE = 0;

    /**
     * 客户经理发送消息给用户
     * @param messageEntity 消息内容
     * @return 消息的id
     */
    Long sendMessageToUser (MessageEntity messageEntity);

    /**
     * 用户发送消息给客服经理
     * @param messageEntity 用户实体信息
     * @return 消息的id
     */
    Long sendMessageForServe(MessageEntity messageEntity);

    /**
     * 分页获取当前排队的用户
     * @param curPage 当前页，从1开始
     * @param pageSize 每页的size
     * @return 当前等待的用户以及用户最后一条信息
     */
    Pager<ChatUserVO> getWaitingUsers(int curPage, int pageSize);


    /**
     * 客户经理接受一个用户
     * @param openId 用户的openId
     * @param id 经理的id
     */
    void receiveUser(String openId, Long id);

    /**
     * 关闭一个用户的连接,使其退出客服
     * @param openId 用户的openId
     * @param id 经理的id
     */
    void disconnectUser(String openId, Long id);

    /**
     * 从一个客户经理转接到另一个客户经理
     * @param openId 用户的id
     * @param from 当前客户经理
     * @param to 转接的客户经理
     */
    void dispatchToOther(String openId, Long from, Long to);

    /**
     * 将消息状态设置为已读
     * @param messageIds 消息的id列表
     * @param agentId 读取的经理的id
     */
    void readMessage(List<Long> messageIds,Long agentId);

    /**
     * 获取用户指定消息前的10条历史记录
     * @param userId 用户id
     * @param messageId 获取的历史记录最后一条的后面一条的id【(1，2，3，4，5)，6】，
     *                  假如消息是如此时间顺序存储，通过id 6可以获取到前面的若干条数据，
     *                  若messageId为null,则获取最新的
     * @param agentId 经理的id
     * @return messageId前的最多10条数据
     */
    List<MessageEntity> getHistoryMessages (String userId, Long agentId, Long messageId);

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
     * 获取经理的所有未读信息的用户
     * @param agentId  经理的id
     * @return 消息列表
     */
    List<ChatUserVO> getAgentUserVOs (Long agentId);
}
