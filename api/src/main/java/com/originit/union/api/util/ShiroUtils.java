package com.originit.union.api.util;

import com.originit.union.entity.SysUserEntity;
import com.originit.common.util.SpringUtil;
import com.originit.union.service.RedisService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authc.LogoutAware;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.crazycake.shiro.RedisSessionDAO;

import java.util.Collection;
import java.util.Objects;

/**
 * @Description Shiro工具类
 * @Author Sans
 * @CreateTime 2019/6/15 16:11
 */
public class ShiroUtils {

	/** 私有构造器 **/
	private ShiroUtils(){ }

    private static RedisSessionDAO redisSessionDAO = SpringUtil.getBean(RedisSessionDAO.class);

	private static RedisService redisService = SpringUtil.getBean(RedisService.class);

    /**
     * 获取当前用户Session
     * @Author Sans
     * @CreateTime 2019/6/17 17:03
     * @Return SysUserEntity 用户信息
     */
    public static Session getSession() {
        return SecurityUtils.getSubject().getSession();
    }

    /**
     * 用户登出
     * @Author Sans
     * @CreateTime 2019/6/17 17:23
     */
    public static void logout() {
        SecurityUtils.getSubject().logout();
    }

	/**
	 * 获取当前用户信息
	 * @Author Sans
	 * @CreateTime 2019/6/17 17:03
	 * @Return SysUserEntity 用户信息
	 */
	public static SysUserEntity getUserInfo() {
		return (SysUserEntity) SecurityUtils.getSubject().getPrincipal();
	}

    /**
     * 删除用户缓存信息
     * @Author Sans
     * @CreateTime 2019/6/17 13:57
     * @Param  userId  用户名称
     * @Param  isRemoveSession 是否删除Session
     * @Return void
     */
    public static void deleteCache(Long userId, boolean isRemoveSession){
        String sessionId = redisService.get(generateUserKey(userId), String.class);
        if (sessionId == null) {
            return;
        }
        //从缓存中获取Session
        final Session session = redisSessionDAO.readSession(sessionId);
        if (session == null) {
            return;
        }
        Object attribute = session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
        if (attribute == null) {
            return;
        }
        //删除session
        if (isRemoveSession) {
            redisSessionDAO.delete(session);
        }
        //删除Cache，在访问受限接口时会重新授权
        DefaultWebSecurityManager securityManager = (DefaultWebSecurityManager) SecurityUtils.getSecurityManager();
        Authenticator authc = securityManager.getAuthenticator();
        ((LogoutAware) authc).onLogout((SimplePrincipalCollection) attribute);
    }

    /**
     * 生成缓存的用户键
     * @param userId 用户id
     * @return 用户的键
     */
    public static String generateUserKey (Long userId) {
        return "union_user_id:" + userId;
    }

}