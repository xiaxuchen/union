package com.originit.union.mq.consumer;

import com.originit.union.entity.MessageEntity;
import com.originit.union.service.ChatService;
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

    private ChatService messageService;

    @Autowired
    public void setMessageService(ChatService messageService) {
        this.messageService = messageService;
    }

    @RabbitHandler
    public void handle(MessageEntity messageEntity, Channel channel, Message message) throws IOException {
        log.info("process orderId:{},curListener:{}",messageEntity,this);
        messageService.sendMessage(messageEntity);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }
}
