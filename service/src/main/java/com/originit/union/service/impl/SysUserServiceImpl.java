package com.originit.union.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.originit.common.exceptions.BusinessException;
import com.originit.common.exceptions.UserException;
import com.originit.common.page.Pager;
import com.originit.common.validator.group.CreateGroup;
import com.originit.union.entity.AgentInfoEntity;
import com.originit.union.entity.SysUserEntity;
import com.originit.union.entity.SysUserRoleEntity;
import com.originit.union.entity.dto.SysUserDto;
import com.originit.union.entity.dto.SysUserQueryDto;
import com.originit.union.entity.dto.SysUserUpdateDto;
import com.originit.union.entity.vo.SysUserVO;
import com.originit.union.mapper.AgentInfoDao;
import com.originit.union.mapper.SysUserDao;
import com.originit.union.mapper.SysUserRoleDao;
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
    public Long addSysUser(@Validated(CreateGroup.class) SysUserDto sysUserDto) {
        // 添加到用户表
        SysUserEntity sysUser = SysUserEntity.builder()
                .headImg(sysUserDto.getHeadImg())
                .username(sysUserDto.getUsername())
                .phone(sysUserDto.getPhone())
                .password(sysUserDto.getPassword())
                .salt(sysUserDto.getSlat())
                .build();
        baseMapper.insert(sysUser);
        if (sysUserDto.getIsAgent()) {
            // 添加客服角色
            sysUserRoleDao.insert(SysUserRoleEntity.builder()
                    .roleId(2L)
                    .userId(sysUser.getUserId())
                    .build());
            // 添加客户经理信息
            agentInfoDao.insert(AgentInfoEntity.builder()
                    .account(sysUserDto.getAccount())
                    .des(sysUserDto.getDes())
                    .name(sysUserDto.getName())
                    .sex(sysUserDto.getSex())
                    .sysUserId(sysUser.getUserId())
                    .build());
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
        final SysUserEntity sysUser = SysUserEntity.builder()
                .headImg(sysUserDto.getHeadImg())
                .username(sysUserDto.getUsername())
                .phone(sysUserDto.getPhone())
                .password(sysUserDto.getPassword())
                .salt(sysUserDto.getSalt())
                .state(sysUserDto.getIsInValid()? SysUserEntity.FORBID:SysUserEntity.ENABLE)
                .gmtModified(LocalDateTime.now())
                .build();
        // 更新系统用户表
        baseMapper.update(sysUser,new QueryWrapper<SysUserEntity>().lambda().
                eq(SysUserEntity::getUserId,sysUserDto.getUserId()));
        final LambdaQueryWrapper<AgentInfoEntity> qw = new QueryWrapper<AgentInfoEntity>().lambda().eq(AgentInfoEntity::getSysUserId, sysUserDto.getUserId());
        if (sysUserDto.getIsAgent()) {
            final AgentInfoEntity agentInfo = AgentInfoEntity.builder()
                    .account(sysUserDto.getAccount())
                    .des(sysUserDto.getDes())
                    .name(sysUserDto.getName())
                    .sex(sysUserDto.getSex())
                    .id(sysUserDto.getAgentId())
                    .sysUserId(sysUserDto.getUserId())
                    .gmtModified(LocalDateTime.now())
                    .build();
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

    @Transactional
    @Override
    public void updatePwd(Long id,String originPwd, String newPwd) {
        if (originPwd.equals(newPwd)) {
            throw new IllegalArgumentException("新旧密码不能一样");
        }
        if (0 == baseMapper.selectCount(new QueryWrapper<SysUserEntity>().lambda()
                .eq(SysUserEntity::getPassword,originPwd).eq(SysUserEntity::getUserId,id))) {
            throw new BusinessException("密码错误");
        }
        // 更新该用户的密码
        if (0 == baseMapper.update(null,new UpdateWrapper<SysUserEntity>().lambda()
                .set(SysUserEntity::getPassword,newPwd)
                .eq(SysUserEntity::getUserId,id))) {
            throw new BusinessException("密码更新失败");
        }
    }
}