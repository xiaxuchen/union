package com.originit.union.api.shiro;

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

    static {
        WECHAT_SESSION.setId(WECHAT_COMMON_SESSION_ID);
    }

    //重写构造器
    public ShiroSessionManager() {
        super();
        this.setDeleteInvalidSessions(true);
    }
    /**
     * 重写方法实现从请求头获取Token便于接口统一
     * 每次请求进来,Shiro会去从请求头找Authorization这个key对应的Value(Token)
     * @Author Sans
     * @CreateTime 2019/6/13 8:47
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
            // 按默认规则从Cookie取Token
             return super.getSessionId(request, response);
        }
    }

    /**
     * 缓存Session到request中
     * @param sessionKey
     * @return
     * @throws UnknownSessionException
     */
    @Override
    protected Session retrieveSession(SessionKey sessionKey) throws UnknownSessionException {
        WebSessionKey webSessionKey = (WebSessionKey) sessionKey;
        final ServletRequest servletRequest = webSessionKey.getServletRequest();
        final Serializable sessionId = webSessionKey.getSessionId();
        // 如果是微信的请求直接返回微信的无效的Session
        if (WECHAT_COMMON_SESSION_ID.equals(sessionId)) {
            return WECHAT_SESSION;
        }
        ServletContext servletContext = null;
        if(sessionId != null && servletRequest != null)
        {
            servletContext = servletRequest.getServletContext();
            if (servletContext != null) {
                Session session =  (Session) servletContext.getAttribute(sessionId.toString());
                if (session != null) {
                    return session;
                }
            }
        }
        final Session session = super.retrieveSession(sessionKey);
        if(servletContext != null)
        {
            if(session != null) {
                servletContext.setAttribute(session.getId().toString(),session);
            }
        }
        return session;
    }
}
