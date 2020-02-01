package com.originit.union.api.controller;

import com.originit.union.entity.SysUserEntity;
import com.originit.union.service.SysUserRoleService;
import com.originit.union.service.SysUserService;
import com.originit.union.api.util.ShiroUtils;
import com.xxc.response.anotation.ResponseResult;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description 用户登录
 * @Author Sans
 * @CreateTime 2019/6/17 15:21
 */
@RestController
@RequestMapping("/manager")
@ResponseResult
public class UserController {

    public static final String TOKEN = "token";

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysUserRoleService sysUserRoleService;

    /**
     * 登录
     * @Author Sans
     * @CreateTime 2019/6/20 9:21
     */
    @RequestMapping("/login")
    @ResponseBody
    public SysUserEntity login(@RequestParam String username, @RequestParam String password,
                               HttpServletRequest request, HttpServletResponse response){
        //验证身份和登陆
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username,password);
        //进行登录操作
        subject.login(token);
        response.setHeader(TOKEN, (String) request.getAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID));
        return ShiroUtils.getUserInfo();
    }

    @GetMapping("/testRole")
    @RequiresRoles("USER")
    @ResponseBody
    public Map<String,String> testRole () {
        Map<String,String> map = new HashMap<>(1);
        map.put("name","good");
        return map;
    }
}