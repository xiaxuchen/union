package com.originit.union.chat.manager;

import com.originit.common.exceptions.DataConflictException;
import com.originit.common.exceptions.DataNotFoundException;
import com.originit.union.chat.manager.function.AgentStateSetter;

import java.util.BitSet;
import java.util.List;

/**
 * 会话管理者，管理用户与客户经理的会话
 * @author xxc、
 */
public interface SessionManager extends Runnable{

    /**
     * 断开客户经理和用户的连接
     * @param userId 用户的id
     * @param agentId 经理的id
     * @param fromUser 是否用户主动
     * @throws DataConflictException 如果该用户状态不是已接入或已被其他人接入则抛出
     */
    void disconnect(String userId,Long agentId) throws DataConflictException;

    /**
     * 连接客户经理和用户
     * @param userId 用户的id
     * @param agentId 经理的id
     * @throws DataConflictException 如果该用户状态不是未接入或已被其他人接入则抛出
     */
    void connect (String userId,Long agentId);

    /**
     * 更新客户经理的信息
     * @param agentId 经理的id
     * @param setter 将会获取经理然后传给setter更新
     * @throws DataNotFoundException 当没有该经理时抛出
     */
    void updateAgent(Long agentId, AgentStateSetter setter) throws DataNotFoundException;


    /**
     * 自动接入用户给客户经理
     */
    @Override
    void run();

    /**
     * 获取经理的所有用户id
     * @param agentId 经理的id
     * @return 经理的当前的所有用户id
     */
    List<String> getUserIds(Long agentId);
}
