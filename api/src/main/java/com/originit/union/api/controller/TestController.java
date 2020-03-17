package com.originit.union.api.controller;

import com.originit.union.api.annotation.Anon;
import com.originit.union.entity.MessageEntity;
import com.originit.union.mq.producer.MessageProducer;
import com.originit.union.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
@Anon
public class TestController {

    @Autowired
    UserService userService;

    @RequestMapping("/users")
    public void importUsers () {
        userService.importUsers();
    }
}
