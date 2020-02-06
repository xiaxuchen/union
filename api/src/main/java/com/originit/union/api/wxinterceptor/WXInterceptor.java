package com.originit.union.api.wxinterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public interface WXInterceptor {

    /**
     * 是否对请求进行拦截
     * @param request 请求
     * @param response 响应
     * @return 是否拦截
     */
    Boolean intercept(HttpServletRequest request, HttpServletResponse response);

    /**
     * 处理请求
     */
    void handle(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
