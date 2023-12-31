package com.originit.union.api.util;

import com.originit.common.util.RedisCacheProvider;
import com.originit.union.entity.SysUserEntity;
import com.originit.common.util.SpringUtil;
import com.originit.union.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authc.LogoutAware;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.crazycake.shiro.RedisSessionDAO;

/**
 * @Description Shiro工具类
 * @Author Sans
 * @CreateTime 2019/6/15 16:11
 */
@Slf4j
public class ShiroUtils {

	/** 私有构造器 **/
	private ShiroUtils(){ }

    private static RedisSessionDAO redisSessionDAO = SpringUtil.getBean(RedisSessionDAO.class);

	private static RedisCacheProvider redisService = SpringUtil.getBean(RedisCacheProvider.class);

	private static RedisCacheProvider provider = SpringUtil.getBean(RedisCacheProvider.class);
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
     * @Param  id  用户名称
     * @Param  isRemoveSession 是否删除Session
     * @Return void
     */
    public static void deleteCache(Long userId, boolean isRemoveSession){
        String sessionId = (String) redisService.get(generateUserKey(userId));
        if (sessionId == null) {
            return;
        }
        Session session = null;
        //从缓存中获取Session
        try {
            session = redisSessionDAO.readSession(sessionId);
        } catch (Exception e) {
            log.error("session with wechatMessageId {} is not exist",sessionId);
        }
        provider.del(generateUserKey(userId));
        log.info("delete session key");
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
            provider.del(sessionId);
            log.info("delete cache for {}",userId);
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


    /**
     * 返回token对应的用户信息
     * @param token 用户token
     * @return 用户信息
     */
    public static SysUserEntity getUserInfo (String token) {
       try {
           return ((SysUserEntity)((SimplePrincipalCollection)redisSessionDAO.readSession
                   (token)
                   .getAttribute("org.apache.shiro.subject.support.DefaultSubjectContext_PRINCIPALS_SESSION_KEY"))
                   .getPrimaryPrincipal());
       } catch (Exception e) {
           return null;
       }
    }

    /**
     * 通过session获取用户信息
     * @param session
     * @return
     */
    public static SysUserEntity getUserInfo (Session session) {
        try {
            return ((SysUserEntity)((SimplePrincipalCollection)session
                    .getAttribute("org.apache.shiro.subject.support.DefaultSubjectContext_PRINCIPALS_SESSION_KEY"))
                    .getPrimaryPrincipal());
        } catch (Exception e) {
            return null;
        }
    }

}