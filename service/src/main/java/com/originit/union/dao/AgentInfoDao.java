package com.originit.union.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.originit.union.entity.AgentInfoEntity;
import com.originit.union.entity.vo.AgentIntroduceVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author xxc、
 */
@Repository
public interface AgentInfoDao extends BaseMapper<AgentInfoEntity> {
    /**
     * 获取经理的信息
     * @param id 系统用户id
     * @return 经理介绍是需要的信息
     */
    AgentIntroduceVO selectAgentInfo(@Param("userId") Long userId);
}
