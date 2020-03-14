package com.originit.union.api.wxinterceptor;

import com.originit.union.constant.WeChatConstant;
import com.soecode.wxtools.bean.WxXmlMessage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface WXInterceptor {

    /**
     * 禁止其他拦截器共享
     */
    int FORRBIDE_OTHER = -1;
    /**
     * 和其他共享
     */
    int SHARED_OTHER = 1;
    /**
     * 不拦截
     */
    int NOT_INTEREST = 0;

    /**
     * 是否对请求进行拦截
     * @param request 请求
     * @param response 响应
     * @return 是否拦截
     */
    int intercept(HttpServletRequest request, HttpServletResponse response) throws Exception;

    /**
     * 处理请求
     */
    void handle(HttpServletRequest request, HttpServletResponse response) throws Exception;

    /**
     * 获取消息
     * @param request 请求中的消息
     * @return 微信用户发来的消息
     */
    default WxXmlMessage getMessage(HttpServletRequest request) {
        return (WxXmlMessage) request.getAttribute(WeChatConstant.ATTR_WEB_XML_MESSAGE);
    }
}
