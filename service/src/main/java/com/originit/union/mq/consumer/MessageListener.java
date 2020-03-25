package com.originit.union.mq.consumer;

import com.originit.union.entity.MessageEntity;
import com.originit.union.service.ChatService;
import com.originit.union.util.DateUtil;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 从消息队列中获取消息，然后操作
 * @author xxc、
 */
@Component
@RabbitListener(
        queues = "union.message.receive"
)
@Slf4j
public class MessageListener {

    private ChatService chatService;

    @Autowired
    public void setChatService(ChatService chatService) {
        this.chatService = chatService;
    }

    @RabbitHandler
    public void handle(MessageEntity messageEntity, Channel channel, Message message) throws IOException {
        log.info("receive chat message,spend {} ms",System.currentTimeMillis() - DateUtil.toTimeMillions(messageEntity.getGmtCreate()));
        chatService.sendMessageForServe(messageEntity);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }
}
