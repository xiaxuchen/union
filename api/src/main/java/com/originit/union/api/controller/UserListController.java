package com.originit.union.api.controller;

import com.originit.union.bussiness.bean.UserInfo;
import com.originit.union.bussiness.bean.UserList;
import com.originit.union.bussiness.bean.service.WXService;
import com.soecode.wxtools.api.IService;
import com.soecode.wxtools.exception.WxErrorException;
import com.xxc.response.anotation.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value="/push")
@ResponseResult
public class UserListController {
    @Autowired
    private IService iService;
    @Autowired
    WXService  wxService;
    @RequestMapping("/userList")
    @ResponseBody
    public UserList getAllRole() throws WxErrorException {
        return wxService.getUserList(null,0,1,1);
    }
}


