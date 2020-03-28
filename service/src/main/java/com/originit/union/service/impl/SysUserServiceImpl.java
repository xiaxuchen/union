package com.originit.union.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.originit.common.exceptions.BusinessException;
import com.originit.common.page.Pager;
import com.originit.common.util.ExceptionUtil;
import com.originit.common.util.SHA256Util;
import com.originit.common.validator.group.CreateGroup;
import com.originit.union.entity.AgentInfoEntity;
import com.originit.union.entity.SysUserEntity;
import com.originit.union.entity.SysUserRoleEntity;
import com.originit.union.entity.UserBindEntity;
import com.originit.union.entity.converter.SysUserConverter;
import com.originit.union.entity.dto.SysUserCreateDto;
import com.originit.union.entity.dto.SysUserQueryDto;
import com.originit.union.entity.dto.SysUserUpdateDto;
import com.originit.union.entity.dto.UserBindDto;
import com.originit.union.entity.vo.SysUserVO;
import com.originit.union.dao.AgentInfoDao;
import com.originit.union.dao.SysUserDao;
import com.originit.union.dao.SysUserRoleDao;
import com.originit.union.exception.file.FileNotFoundException;
import com.originit.union.exception.user.UserAlreadyExistException;
import com.originit.union.exception.user.UserNotExistException;
import com.originit.union.service.FileService;
import com.originit.union.service.SysUserService;
import com.originit.union.util.PagerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import sun.rmi.runtime.Log;

import java.time.LocalDateTime;

/**
 * @Description 系统用户业务实现
 * @Author Sans
 * @CreateTime 2019/6/14 15:57
 */
@Service("sysUserService")
@Slf4j
public class SysUserServiceImpl extends ServiceImpl<SysUserDao, SysUserEntity> implements SysUserService {


    private AgentInfoDao agentInfoDao;

    private SysUserRoleDao sysUserRoleDao;

    private FileService fileService;

    @Autowired
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

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
        // 1. 检验这个用户的用户名是否存在
        if (selectUserByName(sysUserDto.getUsername()) != null) {
            throw new UserAlreadyExistException("该用户已存在");
        }
        // 添加到用户表
        SysUserEntity sysUser = SysUserConverter.INSTANC.to(sysUserDto);
        // 如果有头像但是头像不存在，抛异常
        if (sysUserDto.getHeadImg() != null && !fileService.exists(sysUserDto.getHeadImg())) {
            throw new FileNotFoundException();
        }
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
        IPage<SysUserVO> sysUserVOIPage = baseMapper.selectByConditions(PagerUtil
                .createPage(queryDto.getCurPage(), queryDto.getPageSize()), queryDto)
                .convert(SysUserConverter.INSTANC::to);
        return PagerUtil.fromIPage(sysUserVOIPage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSysUser(SysUserUpdateDto sysUserDto) {
        // 1. 如果有头像但是头像不存在，抛异常
        if (sysUserDto.getHeadImg() != null && !fileService.exists(sysUserDto.getHeadImg())) {
            throw new FileNotFoundException("头像文件不存在或已失效");
        }
        // 2. 获取更新前的用户的头像信息，等到最后删除掉原来的头像，防止空间的浪费
        final SysUserEntity sysUserEntity = baseMapper.selectOne(new QueryWrapper<SysUserEntity>()
                .lambda().select(SysUserEntity::getHeadImg)
                .eq(SysUserEntity::getUserId, sysUserDto.getUserId()));
        if (sysUserEntity == null) {
            throw new UserNotExistException("该用户不存在");
        }
        // 3. 将用户更新实体转换为用户实体
        final SysUserEntity sysUser = SysUserConverter.INSTANC.to(sysUserDto);
        sysUser.setGmtModified(LocalDateTime.now());

        // 4. 如果要对用户的状态进行就转换为对应的状态
        if (sysUserDto.getIsInValid() != null) {
            sysUser .setState(sysUserDto.getIsInValid()? SysUserEntity.FORBID:SysUserEntity.ENABLE);
        }

        // 5. 更新系统用户表
        baseMapper.update(sysUser,new QueryWrapper<SysUserEntity>().lambda().
                eq(SysUserEntity::getUserId,sysUserDto.getUserId()));


        final LambdaQueryWrapper<AgentInfoEntity> qw = new QueryWrapper<AgentInfoEntity>()
                .lambda().eq(AgentInfoEntity::getSysUserId, sysUserDto.getUserId());
        // 6. 如果用户是用户经理，就更新用户经理的信息
        if (sysUserDto.getIsAgent()) {
            // 6.0.1 转化为经理实体
            final AgentInfoEntity agentInfo = SysUserConverter.INSTANC.toAgentInfoEntity(sysUserDto);
            // 将更新时间设为现在
            agentInfo.setGmtModified(LocalDateTime.now());
            // 6.0.2 根据是否存在进行更新或插入
            if (agentInfoDao.selectCount(qw) != 0) {
                // 已存在则更新
                agentInfoDao.update(agentInfo,qw);
            } else {
                // 添加客户经理信息
                agentInfoDao.insert(agentInfo);
            }
        } else {
            // 6.1 如果不是经理则删除经理信息，没有就删除不了
            agentInfoDao.delete(qw);
        }
        // 7. 如果原有头像就删除
        if(sysUserEntity.getHeadImg() != null) {
            try {
                fileService.deleteFile(sysUserEntity.getHeadImg());
            }catch (Exception e) {
                log.error("删除失败,{}", ExceptionUtil.buildErrorMessage(e));
            }
        }
        // 8. 最终，引用用户的头像
        if (sysUserDto.getHeadImg()!=null) {
            fileService.applyFile(sysUserDto.getHeadImg());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updatePwd(Long id,String originPwd, String newPwd) {
        if (originPwd.equals(newPwd)) {
            throw new IllegalArgumentException("新旧密码不能一样");
        }
        // 1. 查找用户的密码
        final SysUserEntity sysUserEntity = baseMapper.selectOne(new QueryWrapper<SysUserEntity>().lambda()
                .select(SysUserEntity::getPassword, SysUserEntity::getSalt)
                .eq(SysUserEntity::getUserId, id));

        // 2. 比对
        if (!sysUserEntity.getPassword().equals(SHA256Util.sha256(originPwd,sysUserEntity.getSalt()))) {
            throw new BusinessException("密码错误");
        }
        // 3. 更新该用户的密码
        if (0 == baseMapper.update(null,new UpdateWrapper<SysUserEntity>().lambda()
                .set(SysUserEntity::getPassword,SHA256Util.sha256(newPwd,sysUserEntity.getSalt()))
                .eq(SysUserEntity::getUserId,id))) {
            throw new BusinessException("密码更新失败");
        }
    }
}