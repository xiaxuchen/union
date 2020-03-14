package com.originit.union.mq.consumer;

import com.originit.union.entity.MessageEntity;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

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

    @RabbitHandler
    public void handle(MessageEntity messageEntity, Channel channel, Message message) throws IOException {
        log.info("process orderId:{},curListener:{}",messageEntity,this);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }
}
