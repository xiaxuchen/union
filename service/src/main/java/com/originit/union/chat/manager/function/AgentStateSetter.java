package com.originit.union.chat.manager.function;

import com.originit.union.chat.data.AgentState;

/**
     * 更新经理信息的接口
     */
    public interface AgentStateSetter {
        void updateAgent (AgentState agentInfo);
    }