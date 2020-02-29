package com.originit.union.api.shiro.filter;

import com.originit.union.api.shiro.ShiroSessionManager;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.web.filter.authc.UserFilter;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 重写shiro的UserFilter，实现通过OPTIONS请求
 * @author MDY
 */
public class ShiroUserFilter extends UserFilter {

    /**
     * 在访问过来的时候检测是否为OPTIONS请求，如果是就直接返回true
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        if (httpRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            setHeader(httpRequest,httpResponse);
            return true;
        }
        return super.preHandle(request,response);
    }

    /**
     * 该方法会在验证失败后调用，这里由于是前后端分离，后台不控制页面跳转
     * 因此重写改成传输JSON数据
     */
    @Override
    protected void saveRequestAndRedirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
        saveRequest(request);
        setHeader((HttpServletRequest) request,(HttpServletResponse) response);
        throw new AuthorizationException("身份验证失败");
    }

    /**
     * 为response设置header，实现跨域
     */
    private void setHeader(HttpServletRequest request, HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Allow-Headers", ShiroSessionManager.AUTHORIZATION + ",content-type,token");
        response.setHeader("Access-Control-Expose-Headers", ShiroSessionManager.AUTHORIZATION + ",content-type,token");
    }

}

