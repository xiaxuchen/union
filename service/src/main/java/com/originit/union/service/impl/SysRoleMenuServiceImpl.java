package com.originit.union.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.originit.union.entity.SysRoleMenuEntity;
import com.originit.union.dao.SysRoleMenuDao;
import com.originit.union.service.SysRoleMenuService;
import org.springframework.stereotype.Service;

/**
 * @Description 角色与权限业务实现
 * @Author Sans
 * @CreateTime 2019/6/14 15:57
 */
@Service("sysRoleMenuService")
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuDao, SysRoleMenuEntity> implements SysRoleMenuService {

}