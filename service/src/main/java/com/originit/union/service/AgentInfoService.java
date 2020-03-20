package com.originit.union.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.originit.union.chat.data.AgentState;
import com.originit.union.entity.AgentInfoEntity;

public interface AgentInfoService extends IService<AgentInfoEntity> {
    /**
     * 获取用户信息到用户状态对象中(包括获取头像)
     * @param userId 用户id
     * @return 用户信息
     */
    AgentState getAgentStateByUserId(Long userId);
}
