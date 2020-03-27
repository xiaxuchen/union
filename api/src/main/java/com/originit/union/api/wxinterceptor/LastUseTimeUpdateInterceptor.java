package com.originit.union.api.wxinterceptor;

import com.originit.common.annotation.Interceptor;
import com.originit.union.constant.WeChatConstant;
import com.originit.union.service.WeChatUserService;
import com.originit.union.util.DateUtil;
import com.soecode.wxtools.api.WxConsts;
import com.soecode.wxtools.bean.WxXmlMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Interceptor
public class LastUseTimeUpdateInterceptor implements WXInterceptor {

    private WeChatUserService weChatUserService;

    @Autowired
    public void setWeChatUserService(WeChatUserService weChatUserService) {
        this.weChatUserService = weChatUserService;
    }

    @Override
    public int intercept(HttpServletRequest request, HttpServletResponse response) throws Exception {
        WxXmlMessage message = (WxXmlMessage) request.getAttribute(WeChatConstant.ATTR_WEB_XML_MESSAGE);
        if (WxConsts.EVT_SUBSCRIBE.equals(message.getEvent())) {
            return WXInterceptor.SHARED_OTHER;
        }
        if (WxConsts.EVT_CLICK.equals(message.getEvent()) || WxConsts.EVT_SCANCODE_PUSH.equals(message.getEvent())) {
            return WXInterceptor.SHARED_OTHER;
        }

        if (message.getEvent() == null || message.getEvent().isEmpty()) {
            return WXInterceptor.SHARED_OTHER;
        }

        return WXInterceptor.NOT_INTEREST;
    }

    @Override
    @Async
    public void handle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        WxXmlMessage message = (WxXmlMessage) request.getAttribute(WeChatConstant.ATTR_WEB_XML_MESSAGE);
        if (WxConsts.EVT_SUBSCRIBE.equals(message.getEvent())) {
            // 如果是订阅消息,就先导入用户
            weChatUserService.importUser(message.getFromUserName());
        }
        weChatUserService.updateLastUseTime(message.getFromUserName(), DateUtil.toLocalDateTime(message.getCreateTime()));
    }
}
