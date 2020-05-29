package com.originit.union.api.wxinterceptor;

import com.originit.common.annotation.Interceptor;
import com.originit.union.entity.MessageEntity;
import com.originit.union.mq.producer.MessageProducer;
import com.originit.union.service.ChatService;
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
 * 当用户消息给服务器，将会根据消息去选择不处理、排队等待用户经理处理、关闭聊天会或者发送消息
 * 只有在聊天中的用户才可以发送聊天消息给当前接代的客户经理
 * 排队中的用户或正在聊天的用户可以关闭会话
 * 不在聊天中的用户可以申请排队
 * @author xxc、
 */
@Interceptor
@Slf4j
public class ChatInterceptor implements WXInterceptor {


    @Autowired
    private MessageProducer producer;

    @Autowired
    private ChatService chatService;

    @Override
    public int intercept(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (this.getMessage(request).getMsgType().equals(WxConsts.CUSTOM_MSG_IMAGE) || this.getMessage(request).getMsgType().equals(WxConsts.CUSTOM_MSG_TEXT)) {
            return WXInterceptor.SHARED_OTHER;
        }
        return WXInterceptor.NOT_INTEREST;
    }

    @Override
    @Async
    public void handle(HttpServletRequest request, HttpServletResponse response, WxXmlMessage message) {
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
                log.error("【用户消息】暂时无法处理该类型消息,type={},id={}", message.getMsgType(), message.getMsgId());
                return;
            }
        }
        // 不使用消息队列，直接使用service
        chatService.sendMessageForServe(MessageEntity.builder()
                .content(content)
                .wechatMessageId(message.getMsgId())
                .fromUser(true)
                .type(msgType)
                .openId(message.getFromUserName())
                .state(MessageEntity.STATE.WAIT)
                .gmtCreate(DateUtil.toLocalDateTime(message.getCreateTime()))
                .build());
        // 将消息打入消息队列
//        producer.sendMessage();
    }
}
