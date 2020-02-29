package com.originit.union.api.wxinterceptor;

import com.originit.common.annotation.Interceptor;
import com.originit.common.util.RedisCacheProvider;
import com.originit.union.bussiness.MessageBusiness;
import com.originit.union.constant.WeChatConstant;
import com.originit.union.entity.domain.PreviewState;
import com.originit.union.util.EventUtil;
import com.soecode.wxtools.api.WxConsts;
import com.soecode.wxtools.bean.WxXmlMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 预览推送消息扫码事件拦截
 */
@Interceptor
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
    public void handle(HttpServletRequest request, HttpServletResponse response) {
        WxXmlMessage wxXmlMessage = (WxXmlMessage) request.getAttribute(WeChatConstant.ATTR_WEB_XML_MESSAGE);
        String eventKey = wxXmlMessage.getEventKey();
        String id = EventUtil.getEventKeyParams(eventKey).get("id");
        final PreviewState previewState = (PreviewState) redisCacheProvider.hget(EVENT_KEY, id);
        if (previewState == null) {
            return;
        }
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
