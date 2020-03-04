package com.originit.union.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.originit.common.page.Pager;
import com.originit.union.entity.SysUserEntity;
import com.originit.union.entity.dto.SysUserDto;
import com.originit.union.entity.dto.SysUserQueryDto;
import com.originit.union.entity.dto.SysUserUpdateDto;
import com.originit.union.entity.vo.SysUserVO;

import java.util.List;

/**
 * @Description 系统用户业务接口
 * @Author Sans
 * @CreateTime 2019/6/14 15:57
 */
public interface SysUserService extends IService<SysUserEntity> {

    /**
     * 根据用户名查询实体
     * @Author Sans
     * @CreateTime 2019/6/14 16:30
     * @Param  username 用户名
     * @Return SysUserEntity 用户实体
     */
    SysUserEntity selectUserByName(String username);

    /**
     * 添加系统用户
     * @param sysUserDto 用户信息，若为客户经理则添加客户经理信息
     * @return 用户id
     */
    Long addSysUser(SysUserDto sysUserDto);

    /**
     * 通过条件搜索
     * @param queryDto 查询的dto
     * @return 用户信息列表
     */
    Pager<SysUserVO> search(SysUserQueryDto queryDto);

    /**
     * 更新系统用户信息
     * @param sysUserEntity 系统用户信息
     */
    void updateSysUser (SysUserUpdateDto sysUserEntity);
}

