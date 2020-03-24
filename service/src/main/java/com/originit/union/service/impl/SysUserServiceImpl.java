package com.originit.union.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.originit.common.exceptions.BusinessException;
import com.originit.common.page.Pager;
import com.originit.common.util.SHA256Util;
import com.originit.common.validator.group.CreateGroup;
import com.originit.union.entity.AgentInfoEntity;
import com.originit.union.entity.SysUserEntity;
import com.originit.union.entity.SysUserRoleEntity;
import com.originit.union.entity.converter.SysUserConverter;
import com.originit.union.entity.dto.SysUserCreateDto;
import com.originit.union.entity.dto.SysUserQueryDto;
import com.originit.union.entity.dto.SysUserUpdateDto;
import com.originit.union.entity.vo.SysUserVO;
import com.originit.union.dao.AgentInfoDao;
import com.originit.union.dao.SysUserDao;
import com.originit.union.dao.SysUserRoleDao;
import com.originit.union.service.SysUserService;
import com.originit.union.util.PagerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

/**
 * @Description 系统用户业务实现
 * @Author Sans
 * @CreateTime 2019/6/14 15:57
 */
@Service("sysUserService")
public class SysUserServiceImpl extends ServiceImpl<SysUserDao, SysUserEntity> implements SysUserService {


    private AgentInfoDao agentInfoDao;

    private SysUserRoleDao sysUserRoleDao;

    @Autowired
    public void setSysUserRoleDao(SysUserRoleDao sysUserRoleDao) {
        this.sysUserRoleDao = sysUserRoleDao;
    }

    @Autowired
    public void setAgentInfoDao(AgentInfoDao agentInfoDao) {
        this.agentInfoDao = agentInfoDao;
    }

    /**
     * 根据用户名查询实体
     * @Author Sans
     * @CreateTime 2019/6/14 16:30
     * @Param  username 用户名
     * @Return SysUserEntity 用户实体
     */
    @Override
    public SysUserEntity selectUserByName(String username) {
        QueryWrapper<SysUserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUserEntity::getUsername,username);
        return this.baseMapper.selectOne(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addSysUser(@Validated(CreateGroup.class) SysUserCreateDto sysUserDto) {
        // 添加到用户表
        SysUserEntity sysUser = SysUserConverter.INSTANC.to(sysUserDto);
        baseMapper.insert(sysUser);
        if (sysUserDto.getIsAgent()) {
            // 添加客服角色
            sysUserRoleDao.insert(SysUserRoleEntity.builder()
                    .roleId(2L)
                    .userId(sysUser.getUserId())
                    .build());
            final AgentInfoEntity agentInfo = SysUserConverter.INSTANC.toAgentInfoEntity(sysUserDto);
            agentInfo.setSysUserId(sysUser.getUserId());
            // 添加客户经理信息
            agentInfoDao.insert(agentInfo);
        }
        return sysUser.getUserId();
    }

    @Override
    public Pager<SysUserVO> search(SysUserQueryDto queryDto) {
        IPage<SysUserVO> sysUserVOIPage = baseMapper.selectByConditions(PagerUtil.createPage(queryDto.getCurPage(), queryDto.getPageSize()), queryDto);
        return PagerUtil.fromIPage(sysUserVOIPage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSysUser(SysUserUpdateDto sysUserDto) {
        final SysUserEntity sysUser = SysUserConverter.INSTANC.to(sysUserDto);
        sysUser.setGmtModified(LocalDateTime.now());
        if (sysUserDto.getIsInValid() != null) {
            sysUser .setState(sysUserDto.getIsInValid()? SysUserEntity.FORBID:SysUserEntity.ENABLE);
        }

        // 更新系统用户表
        baseMapper.update(sysUser,new QueryWrapper<SysUserEntity>().lambda().
                eq(SysUserEntity::getUserId,sysUserDto.getUserId()));
        final LambdaQueryWrapper<AgentInfoEntity> qw = new QueryWrapper<AgentInfoEntity>().lambda().eq(AgentInfoEntity::getSysUserId, sysUserDto.getUserId());
        if (sysUserDto.getIsAgent()) {
            final AgentInfoEntity agentInfo = SysUserConverter.INSTANC.toAgentInfoEntity(sysUserDto);
            // 将更新时间设为现在
            agentInfo.setGmtModified(LocalDateTime.now());
            if (agentInfoDao.selectCount(qw) != 0) {
                // 已存在则更新
                agentInfoDao.update(agentInfo,qw);
            } else {
                // 添加客户经理信息
                agentInfoDao.insert(agentInfo);
            }
        } else {
            // 如果不是经理则删除经理信息，没有就删除不了
            agentInfoDao.delete(qw);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updatePwd(Long id,String originPwd, String newPwd) {
        if (originPwd.equals(newPwd)) {
            throw new IllegalArgumentException("新旧密码不能一样");
        }
        final SysUserEntity sysUserEntity = baseMapper.selectOne(new QueryWrapper<SysUserEntity>().lambda()
                .select(SysUserEntity::getPassword, SysUserEntity::getSalt)
                .eq(SysUserEntity::getUserId, id));

        if (!sysUserEntity.getPassword().equals(SHA256Util.sha256(originPwd,sysUserEntity.getSalt()))) {
            throw new BusinessException("密码错误");
        }
        // 更新该用户的密码
        if (0 == baseMapper.update(null,new UpdateWrapper<SysUserEntity>().lambda()
                .set(SysUserEntity::getPassword,SHA256Util.sha256(newPwd,sysUserEntity.getSalt()))
                .eq(SysUserEntity::getUserId,id))) {
            throw new BusinessException("密码更新失败");
        }
    }
}