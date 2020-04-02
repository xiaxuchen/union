package com.originit.union.api.shiro;

import com.originit.common.util.RedisCacheProvider;
import com.originit.common.util.SpringUtil;
import com.originit.union.api.shiro.config.ShiroConfig;
import com.originit.union.api.util.ShiroUtils;
import com.originit.union.entity.SysUserEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;
import org.apache.shiro.web.util.WebUtils;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * @Description 自定义获取Token
 * @Author Sans
 * @CreateTime 2019/6/13 8:34
 */
@Slf4j
public class ShiroSessionManager extends DefaultWebSessionManager {

    //定义常量
    public static final String AUTHORIZATION = "Authorization";
    public static final String REFERENCED_SESSION_ID_SOURCE = "Stateless request";

    // 微信端公众的session
    public static final String WECHAT_COMMON_SESSION_ID = "we-chat-common-session";

    private static final SimpleSession WECHAT_SESSION = new SimpleSession();

    private RedisCacheProvider provider;

    @Autowired
    public void setProvider(RedisCacheProvider provider) {
        this.provider = provider;
    }

    public ShiroSessionManager (RedisSessionDAO redisSessionDAO) {
        this();
        setSessionDAO(redisSessionDAO);
        // 一开始会去存储微信的公用session
        WECHAT_SESSION.setId(WECHAT_COMMON_SESSION_ID);
        redisSessionDAO.update(WECHAT_SESSION);
        log.info("保存微信端的公共session");
    }

    //重写构造器
    public ShiroSessionManager() {
        super();
        this.setDeleteInvalidSessions(true);
    }
    /**
     * 这里是获取sesionId,重写方法实现从请求头获取Token便于接口统一
     * 每次请求进来,Shiro会去从请求头找Authorization这个key对应的Value(Token)
     */
    @Override
    public Serializable getSessionId(ServletRequest request, ServletResponse response) {
        // 如果是来自微信的，就不单独创建session
        if (request.getParameter("signature") != null
                && request.getParameter("timestamp") != null
                && request.getParameter("nonce") != null ) {
            return WECHAT_COMMON_SESSION_ID;
        }
        String token = WebUtils.toHttp(request).getHeader(AUTHORIZATION);
        //如果请求头中存在token 则从请求头中获取token
        if (!StringUtils.isEmpty(token)) {
            ((HttpServletResponse)response).setHeader(AUTHORIZATION,token);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, REFERENCED_SESSION_ID_SOURCE);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, token);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
            return token;
        } else {
             return null;
        }
    }


    /**
     * 获取session
     * @param sessionKey
     * @return
     * @throws UnknownSessionException
     */
    @Override
    protected Session retrieveSession(SessionKey sessionKey) throws UnknownSessionException {
        Serializable sessionId = getSessionId(sessionKey);
        // 如果是微信的请求直接返回微信的无效的Session
        if (WECHAT_COMMON_SESSION_ID.equals(sessionId)) {
            return WECHAT_SESSION;
        }
        ServletRequest request = null;
            if (sessionKey instanceof WebSessionKey){
            request = (ServletRequest) ((WebSessionKey) sessionKey).getServletRequest();
        }
            if (request!=null&&sessionId!=null) {
            Session session = (Session) request.getAttribute(sessionId.toString());
            if(session!=null)
            {
                return session;
            }
        }
        Session session = super.retrieveSession(sessionKey);
            if(request!=null &&sessionId!=null){
            request.setAttribute(sessionId.toString(),session);
        }
        // 刷新用户的session键
        final SysUserEntity userInfo = ShiroUtils.getUserInfo(session);
            if (userInfo != null) {
            final String userKey = ShiroUtils.generateUserKey(userInfo.getUserId());
            provider.expire(userKey, ShiroConfig.EXPIRE);
            log.info("刷新用户的session键(用户快速查找用户的session)");
        }
        return session;
    }
}
