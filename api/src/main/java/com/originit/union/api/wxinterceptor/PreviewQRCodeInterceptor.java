package com.originit.union.api.wxinterceptor;

import com.originit.common.annotation.Interceptor;
import com.originit.common.util.RedisCacheProvider;
import com.originit.union.bussiness.MessageBusiness;
import com.originit.union.constant.WeChatConstant;
import com.originit.union.entity.domain.PreviewState;
import com.originit.union.util.EventUtil;
import com.soecode.wxtools.api.WxConsts;
import com.soecode.wxtools.bean.WxXmlMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 预览推送消息扫码事件拦截
 * 当客户经理请求预览就会生成二维码，扫描二维码手机会发出事件给服务器，由该拦截器拦截，根据id更新二维码状态
 */
@Interceptor
@Slf4j
public class PreviewQRCodeInterceptor implements WXInterceptor {

    private MessageBusiness messageBusiness;

    @Autowired
    public void setMessageBusiness(MessageBusiness messageBusiness) {
        this.messageBusiness = messageBusiness;
    }

    public static final String EVENT_KEY = "push/preview";

    private RedisCacheProvider redisCacheProvider;

    @Autowired
    public void setRedisCacheProvider(RedisCacheProvider redisCacheProvider) {
        this.redisCacheProvider = redisCacheProvider;
    }

    @Override
    public int intercept(HttpServletRequest request, HttpServletResponse response) {
        WxXmlMessage wxXmlMessage = (WxXmlMessage) request.getAttribute(WeChatConstant.ATTR_WEB_XML_MESSAGE);
        if (wxXmlMessage.getMsgType().equals(WxConsts.XML_MSG_EVENT) &&
                wxXmlMessage.getEvent().equals(WxConsts.EVT_SCAN) &&
        wxXmlMessage.getEventKey().startsWith(EVENT_KEY)) {
            return FORRBIDE_OTHER;
        }
        return NOT_INTEREST;
    }

    @Override
    @Async
    public void handle(HttpServletRequest request, HttpServletResponse response,WxXmlMessage wxXmlMessage) {
        String eventKey = wxXmlMessage.getEventKey();
        String id = EventUtil.getEventKeyParams(eventKey).get("id");
        final PreviewState previewState = (PreviewState) redisCacheProvider.hget(EVENT_KEY, id);
        if (previewState == null) {
            return;
        }
        // id是在客户经理发送请求的时候生成的
        log.info("【预览推送二维码】生成二维码,id={}", id);
        // 发送预览消息，并校正状态
        ListenableFuture<Long> future = messageBusiness.preview(wxXmlMessage.getFromUserName(), previewState.getType(), previewState.getContent());
        future.addCallback(new ListenableFutureCallback<Long>() {
            @Override
            public void onFailure(Throwable throwable) { }

            @Override
            public void onSuccess(Long aLong) {
                // 成功则设置为已推送
                previewState.setSuccess(true);
                redisCacheProvider.hset(EVENT_KEY, id,previewState);
            }
        });

    }
}
