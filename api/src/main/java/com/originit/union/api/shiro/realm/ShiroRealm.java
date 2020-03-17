package com.originit.union.api.shiro.realm;

//import com.originit.union.entity.SysMenuEntity;
//import com.originit.union.entity.SysRoleEntity;
//import com.originit.union.entity.SysUserEntity;
//import com.originit.union.service.SysMenuService;
//import com.originit.union.service.SysRoleService;
//import com.originit.union.service.SysUserService;
//import com.originit.union.api.util.ShiroUtils;
//import org.apache.shiro.authc.*;
//import org.apache.shiro.authz.AuthorizationInfo;
//import org.apache.shiro.authz.SimpleAuthorizationInfo;
//import org.apache.shiro.realm.AuthorizingRealm;
//import org.apache.shiro.subject.PrincipalCollection;
//import org.apache.shiro.util.ByteSource;
//import org.springframework.beans.factory.annotation.Autowired;
//import sun.net.www.protocol.http.AuthenticationInfo;
//
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;

import com.originit.common.enums.ResultCode;
import com.originit.common.exceptions.UserException;
import com.originit.common.exceptions.UserNotLoginException;
import com.originit.union.api.util.ShiroUtils;
import com.originit.union.entity.SysMenuEntity;
import com.originit.union.entity.SysRoleEntity;
import com.originit.union.entity.SysUserEntity;
import com.originit.union.service.RedisService;
import com.originit.union.service.SysMenuService;
import com.originit.union.service.SysRoleService;
import com.originit.union.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Description Shiro权限匹配和账号密码匹配
 * @Author Sans
 * @CreateTime 2019/6/15 11:27
 */
@Slf4j
public class ShiroRealm extends AuthorizingRealm {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysMenuService sysMenuService;

    /**
     * 授权权限
     * 用户进行权限验证时候Shiro会去缓存中找,如果查不到数据,会执行这个方法去查权限,并放入缓存中
     * @Author Sans
     * @CreateTime 2019/6/12 11:44
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        SysUserEntity sysUserEntity = (SysUserEntity) principalCollection.getPrimaryPrincipal();
        //获取用户ID
        Long userId =sysUserEntity.getUserId();
        //这里可以进行授权和处理
        Set<String> rolesSet = new HashSet<>();
        Set<String> permsSet = new HashSet<>();
        //查询角色和权限(这里根据业务自行查询)
        List<SysRoleEntity> sysRoleEntityList = sysRoleService.selectSysRoleByUserId(userId);
        for (SysRoleEntity sysRoleEntity:sysRoleEntityList) {
            rolesSet.add(sysRoleEntity.getRoleName());
            List<SysMenuEntity> sysMenuEntityList = sysMenuService.selectSysMenuByRoleId(sysRoleEntity.getRoleId());
            for (SysMenuEntity sysMenuEntity :sysMenuEntityList) {
                permsSet.add(sysMenuEntity.getPerms());
            }
        }
        //将查到的权限和角色分别传入authorizationInfo中
        authorizationInfo.setStringPermissions(permsSet);
        authorizationInfo.setRoles(rolesSet);
        return authorizationInfo;
    }

    /**
     * 身份认证
     * @Author Sans
     * @CreateTime 2019/6/12 12:36
     */

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //获取用户的输入的账号.
        String username = (String) authenticationToken.getPrincipal();
        //通过username从数据库中查找 User对象，如果找到进行验证
        //实际项目中,这里可以根据实际情况做缓存,如果不做,Shiro自己也是有时间间隔机制,2分钟内不会重复执行该方法
        SysUserEntity user = sysUserService.selectUserByName(username);
        //判断账号是否存在
        if (user == null) {
            throw new UserException(ResultCode.USER_NOT_EXIST);
        }
        //判断账号是否被冻结
        if (user.getState()==null || SysUserEntity.FORBID == user.getState()){
            throw new UserException(ResultCode.USER_ACCOUNT_FORBIDDEN);
        }
        //进行验证
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                //用户名
                user,
                //密码
                user.getPassword(),
                //设置盐值
                ByteSource.Util.bytes(user.getSalt()),
                getName()
        );
        //验证成功开始踢人(清除缓存和Session)
        try {
            ShiroUtils.deleteCache(user.getUserId(),true);
        } catch (Exception e) {
            log.error("delete cache fail,the message is:{}",e.getMessage());
        }
        return authenticationInfo;
    }
}
