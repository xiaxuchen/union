package com.originit.union.api.shiro;

import com.xxc.common.utils.RequestContextHolderUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.text.MessageFormat;

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
        String token = WebUtils.toHttp(request).getHeader(AUTHORIZATION);
        log.info("获取sessionId,请求头中原token为:" + token);
        //如果请求头中存在token 则从请求头中获取token
        if (!StringUtils.isEmpty(token)) {
            ((HttpServletResponse)response).setHeader(AUTHORIZATION,token);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, REFERENCED_SESSION_ID_SOURCE);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, token);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
            return token;
        } else {
            // 这里禁用掉Cookie获取方式
            // 按默认规则从Cookie取Token
//             return super.getSessionId(request, response);
            return null;
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
        log.info(MessageFormat.format("获取session,其sessionId为{0},请求中的sessionId为{1}",
                sessionKey.getSessionId(), RequestContextHolderUtil.getRequest().getHeader(AUTHORIZATION)));
        WebSessionKey webSessionKey = (WebSessionKey) sessionKey;
        final ServletRequest servletRequest = webSessionKey.getServletRequest();
        final Serializable sessionId = webSessionKey.getSessionId();
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
