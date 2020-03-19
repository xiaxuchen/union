package com.originit.union.chat.manager;

import com.originit.common.page.Pager;
import com.originit.union.chat.data.ChatUser;

import java.util.List;

/**
 * 用户管理者，管理用户状态以及用户信息
 */
public interface UserManager {

    /**
     * 更改用户的状态
     * @param agentId 经理的id
     * @param state 用户的状态
     * @param userId 用户的id
     * @return 是否更改了
     */
    boolean changeState(String userId, Integer state, Long agentId);

    /**
     * 通过id获取用户状态
     * @param userId 用户id
     * @return 用户的状态值
     */
    Integer getUserState(String userId);

    /**
     * 通过用户id获取用户信息
     * @param userId 用户id
     * @return 用户信息
     */
    ChatUser getUser(String userId);

    /**
     * 获取指定客户经理的聊天用户
     * @param agentId 客户经理id
     * @param count
     * @return 聊天列表
     */
    List<ChatUser> getChattingUsers(Long agentId, int count);

    /**
     * 分页获取正在等待的用户
     * @param curPage 当前页，初始为1
     * @param pageSize 每页的大小
     * @return
     */
    Pager<ChatUser> getWaitingUsers(int curPage, int pageSize);

}
