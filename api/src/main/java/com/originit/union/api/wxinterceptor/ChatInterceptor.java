package com.originit.union.api.wxinterceptor;

import com.originit.common.annotation.Interceptor;
import com.originit.union.api.chat.ChatDoor;
import com.originit.union.api.chat.data.Message;
import com.originit.union.entity.MessageEntity;
import com.originit.union.mq.producer.MessageProducer;
import com.originit.union.util.DateUtil;
import com.soecode.wxtools.api.WxConsts;
import com.soecode.wxtools.bean.WxXmlMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户聊天的拦截
 * @author xxc、
 */
@Interceptor
public class ChatInterceptor implements WXInterceptor {


    private MessageProducer producer;

    @Autowired
    public void setProducer(MessageProducer producer) {
        this.producer = producer;
    }

    @Override
    public int intercept(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (this.getMessage(request).getMsgType().equals(WxConsts.CUSTOM_MSG_TEXT)) {
            return WXInterceptor.SHARED_OTHER;
        }
        return WXInterceptor.NOT_INTEREST;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) {
        WxXmlMessage message = getMessage(request);
        // 将消息打入消息队列
        producer.sendMessage(MessageEntity.builder()
                .content(message.getContent())
                .fromUser(true)
                .type(MessageEntity.TYPE.TEXT)
                .userId(message.getFromUserName())
                .state(MessageEntity.STATE.WAIT)
                .gmtCreate(DateUtil.toLocalDateTime(message.getCreateTime()))
                .build());
    }
}
