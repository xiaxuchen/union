package com.originit.union.api.controller;

import com.originit.union.bussiness.bean.TagList;
import com.originit.union.bussiness.bean.UserList;
import com.originit.union.bussiness.bean.service.WXService;
import com.soecode.wxtools.api.IService;
import com.soecode.wxtools.exception.WxErrorException;
import com.xxc.response.anotation.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value="/push")
@ResponseResult
public class UserListController {
    @Autowired
    private IService iService;
    @Autowired
    WXService  wxService;
    String TAG_POST=" https://api.weixin.qq.com/cgi-bin/tags/create?access_token=ACCESS_TOKEN";
    String GET_TAG="https://api.weixin.qq.com/cgi-bin/tags/get?access_token=ACCESS_TOKEN";
    @RequestMapping("/userList")
    @ResponseBody
    /*public UserList getAllRole(@RequestParam String token, @RequestParam int tagList,@RequestParam int curPage,@RequestParam int pageSize) throws WxErrorException {
        return wxService.getUserList(token,tagList,curPage,pageSize);
    }*/
    public UserList getAllRole() throws WxErrorException {
        return wxService.getUserList(null,1,2,3);
    }
    @RequestMapping("/tagList")
    @ResponseBody
    public List<TagList> getTagList() throws WxErrorException, IOException {
       return  wxService.getTagList();
    }
}


