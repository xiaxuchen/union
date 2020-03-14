package com.originit.union.api.controller;

import com.originit.union.entity.MessageEntity;
import com.originit.union.mq.producer.MessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
public class TestController {

    @Autowired
    MessageProducer producer;

    @RequestMapping("/message")
    public void sendMessage () {
        producer.sendMessage(MessageEntity.builder()
                .content("你好")
                .type(0)
                .agentId("123456")
                .build());
    }
}
