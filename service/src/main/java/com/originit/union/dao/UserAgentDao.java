package com.originit.union.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.originit.union.entity.UserAgentEntity;
import com.originit.union.entity.domain.UserInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAgentDao extends BaseMapper<UserAgentEntity> {

    /**
     * 通过所属的经理获取用户信息
     * @param page 分页
     * @param agentId 所属的用户经理
     * @return
     */
    Page<UserInfo> selectByAgentId(Page<?> page, @Param("userId") Long agentId);
}
