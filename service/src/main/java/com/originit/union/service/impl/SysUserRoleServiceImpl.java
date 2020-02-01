package com.originit.union.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.originit.union.entity.SysUserRoleEntity;
import com.originit.union.mapper.SysUserRoleDao;
import com.originit.union.service.SysUserRoleService;
import org.springframework.stereotype.Service;

/**
 * @Description 用户与角色业务实现
 * @Author Sans
 * @CreateTime 2019/6/14 15:57
 */
@Service("sysUserRoleService")
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleDao, SysUserRoleEntity> implements SysUserRoleService {

}