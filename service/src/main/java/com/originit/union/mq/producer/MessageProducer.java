package com.originit.union.mq.producer;

import com.originit.union.entity.MessageEntity;
import com.originit.union.entity.enums.QueueEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageProducer {
    private static Logger LOGGER = LoggerFactory.getLogger(MessageProducer.class);
    private AmqpTemplate amqpTemplate;

    @Autowired
    public void setAmqpTemplate(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    public void sendMessage(MessageEntity messageEntity){
        //给延迟队列发送消息
        amqpTemplate.convertAndSend(QueueEnum.QUEUE_MESSAGE_HANDLE.getExchange(), QueueEnum.QUEUE_MESSAGE_HANDLE.getRouteKey(),messageEntity);
        LOGGER.info("send orderId:{}",messageEntity);
    }
}
