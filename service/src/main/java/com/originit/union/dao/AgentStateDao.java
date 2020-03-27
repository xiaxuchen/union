package com.originit.union.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.originit.union.entity.AgentStateEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 管理经理聊天状态
 * @author xxc、
 */
@Repository
public interface AgentStateDao extends BaseMapper<AgentStateEntity> {
    /**
     * 查找适合的经理的id
     * @param phone 用户的电话
     * @return 经理的信息
     */
    Long selectSuitAgentId(@Param("phone") String phone);
}
