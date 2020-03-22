package com.originit.union.api.controller;

import com.originit.union.api.annotation.Anon;
import com.originit.union.service.WeChatUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
@Anon
public class TestController {

    @Autowired
    WeChatUserService userService;

    @RequestMapping("/users")
    public void importUsers () {
        userService.importUsers();
    }
}
