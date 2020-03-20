package com.originit.union.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.originit.union.chat.data.AgentState;
import com.originit.union.entity.AgentInfoEntity;
import com.originit.union.entity.SysUserEntity;
import com.originit.union.mapper.AgentInfoDao;
import com.originit.union.mapper.SysUserDao;
import com.originit.union.service.AgentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author xxc、
 */
@Service
public class AgentInfoServiceImpl extends ServiceImpl<AgentInfoDao,AgentInfoEntity> implements AgentInfoService {

    private SysUserDao sysUserDao;

    @Autowired
    public void setSysUserDao(SysUserDao sysUserDao) {
        this.sysUserDao = sysUserDao;
    }

    @Override
    public AgentState getAgentStateByUserId(Long userId) {
        AgentInfoEntity agentInfo = this.getOne(new QueryWrapper<AgentInfoEntity>().lambda().eq(AgentInfoEntity::getSysUserId, userId));
        return AgentState.builder()
                .info(agentInfo)
                .headImg(sysUserDao.selectOne(new QueryWrapper<SysUserEntity>().lambda()
                        .select(SysUserEntity::getHeadImg).eq(SysUserEntity::getUserId,userId))
                        .getHeadImg())
                .build();
    }

    /**
     * 通过用户id获取经理的信息
     * @param userId 用户id
     * @return
     */
    public AgentInfoEntity getByUserId (Long userId) {
        return this.getOne(new QueryWrapper<AgentInfoEntity>().lambda().eq(AgentInfoEntity::getSysUserId, userId));
    }
}
