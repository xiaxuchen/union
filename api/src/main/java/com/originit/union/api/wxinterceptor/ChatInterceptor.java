package com.originit.union.api.wxinterceptor;

import com.originit.common.annotation.Interceptor;
import com.originit.union.entity.MessageEntity;
import com.originit.union.mq.producer.MessageProducer;
import com.originit.union.util.DateUtil;
import com.soecode.wxtools.api.WxConsts;
import com.soecode.wxtools.bean.WxXmlMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户聊天的拦截
 * @author xxc、
 */
@Interceptor
@Slf4j
public class ChatInterceptor implements WXInterceptor {


    private MessageProducer producer;

    @Autowired
    public void setProducer(MessageProducer producer) {
        this.producer = producer;
    }

    @Override
    public int intercept(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (this.getMessage(request).getMsgType().equals(WxConsts.CUSTOM_MSG_IMAGE) || this.getMessage(request).getMsgType().equals(WxConsts.CUSTOM_MSG_TEXT)) {
            return WXInterceptor.SHARED_OTHER;
        }
        return WXInterceptor.NOT_INTEREST;
    }

    @Override
    @Async
    public void handle(HttpServletRequest request, HttpServletResponse response) {
        WxXmlMessage message = getMessage(request);
        // 添加图片类型的消息处理
        Integer msgType;
        String content;
        switch (message.getMsgType()) {
            case WxConsts.CUSTOM_MSG_IMAGE: {
                msgType = MessageEntity.TYPE.IMAGE;
                content = message.getPicUrl();
                break;
            }
            case WxConsts.CUSTOM_MSG_TEXT: {
                msgType = MessageEntity.TYPE.TEXT;
                content = message.getContent();
                break;
            }
            default: {
                log.error("message can't handle with wechatMessageId:" + message.getMsgId());
                return;
            }
        }
        // 将消息打入消息队列
        producer.sendMessage(MessageEntity.builder()
                .content(content)
                .wechatMessageId(message.getMsgId())
                .fromUser(true)
                .type(msgType)
                .openId(message.getFromUserName())
                .state(MessageEntity.STATE.WAIT)
                .gmtCreate(DateUtil.toLocalDateTime(message.getCreateTime()))
                .build());
    }
}
