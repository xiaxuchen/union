package com.originit.union.api.wxinterceptor;

import com.originit.common.util.SpringUtil;
import com.originit.union.constant.WeChatConstant;
import com.soecode.wxtools.bean.WxXmlMessage;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.scheduling.annotation.Async;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Action;

public interface WXInterceptor {

    /**
     * 禁止其他拦截器共享，当同时有两个拦截器对一个请求返回该值，将抛出异常，帮助开发人员修改
     */
    int FORRBIDE_OTHER = -1;
    /**
     * 拦截信息并与其他拦截的拦截器共享
     */
    int SHARED_OTHER = 1;
    /**
     * 不拦截，不会执行handle方法
     */
    int NOT_INTEREST = 0;

    /**
     * 执行顺序，越小越靠前
     * @return
     */
    default int order () {
        return 0;
    }
    /**
     * 是否对请求进行拦截
     * @param request 请求
     * @param response 响应
     * @return 是否拦截
     */
    int intercept(HttpServletRequest request, HttpServletResponse response) throws Exception;

    /**
     * 处理请求,如果要使用异步同时又要使用request、response需要异常小心，因为可能这两个对象的使用没有结束对象就关闭了
     */
    void handle(HttpServletRequest request, HttpServletResponse response, WxXmlMessage message) throws Exception;

    /**
     * 获取消息
     * @param request 请求中的消息
     * @return 微信用户发来的消息
     */
    default WxXmlMessage getMessage(HttpServletRequest request) {
        return (WxXmlMessage) request.getAttribute(WeChatConstant.ATTR_WEB_XML_MESSAGE);
    }


}
