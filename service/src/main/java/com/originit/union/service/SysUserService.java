package com.originit.union.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.originit.union.entity.SysUserEntity;
import com.originit.union.entity.dto.SysUserDto;

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
     * 管理员登入
     * @param name  用户名
     * @param password   用户密码
     */
    void sysUserLogin(String name,String password);

    /**
     * 获取角色列表
     * @return  用户角色列表
     */
    List<SysUserDto> getAllSysUser();

    /**
     * 删除多个系统用户
     * @param id   用户的id列表（一个或者多个)
     */
    void  deleteSysUserById(List<String> id);

    /**
     *添加系统用户到数据库中，如果isAgent字段为true，则同时为其绑定客户经理的角色，并插入对应的客户经理的信息到数据库
     * @param sysUserDto
     */
    void  addSysUserInfo(SysUserDto sysUserDto);

    /**
     * 修改系统用户的信息
     * @param sysUserDto
     */
    void  updateSysUser(SysUserDto sysUserDto);
}

