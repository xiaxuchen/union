package com.originit.union.api.interceptor;

import com.originit.common.exceptions.UserNotLoginException;
import com.originit.union.api.annotation.Anon;
import com.originit.union.constant.ShiroConstant;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * shiro中没有特殊处理的路径全部有auth规则处理
 * 这里添加了{@link Anon}注解将请求标注为无需登录的请求
 * @author xxc、
 */
public class ShiroAnnotationInterceptor implements HandlerInterceptor {

    /**
     *
     * @param handler 如果是SpringMvc请求他才是HandlerMethod
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果是背AUTH规则拦截的，就会有这个属性
        if (null == request.getAttribute(ShiroConstant.SHIRO_AUTH_RESULT)) {
            return true;
        }
        //如果是SpringMVC请求,则获取方法或类上的注解，如果有就放行
        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Anon methodAnnotation = handlerMethod.getMethodAnnotation(Anon.class);
            if (methodAnnotation != null) {
                return true;
            }
            Anon anon = handlerMethod.getBeanType().getAnnotation(Anon.class);
            if (anon != null) {
                return true;
            }
        }
        // 如果不允许通过就抛出用户未登录异常
        throw new UserNotLoginException();
    }
}
