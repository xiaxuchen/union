package com.originit.union.api.wxinterceptor;

import com.originit.common.annotation.Interceptor;
import com.originit.union.constant.WeChatConstant;
import com.originit.union.service.WeChatUserService;
import com.originit.union.util.DateUtil;
import com.soecode.wxtools.api.WxConsts;
import com.soecode.wxtools.bean.WxXmlMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 当用户和公众号交互的时候会发出事件，对该事件拦截，并更新用户操作时间，如果用户新订阅，则导入用户
 * 因为聊天只能48小时之内反聊天
 * 所以需要更新用户上次使用的时间
 */
@Slf4j
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
    public void handle(HttpServletRequest request, HttpServletResponse response,WxXmlMessage message) throws Exception {
        if (WxConsts.EVT_SUBSCRIBE.equals(message.getEvent())) {
            log.info("【用户订阅】导入用户id:{}",message.getFromUserName());
            // 如果是订阅消息,就先导入用户
            weChatUserService.importUser(message.getFromUserName());
        } else {
            log.info("【用户交互】交互用户id:{}",message.getFromUserName());
        }
        weChatUserService.updateLastUseTime(message.getFromUserName(), DateUtil.toLocalDateTime(message.getCreateTime()));
    }
}
