package com.originit.union.api.controller;

import com.originit.union.business.WxBusiness;
import com.originit.union.entity.SysUserEntity;
import com.originit.union.entity.dto.UserBindDto;
import com.originit.union.api.util.ShiroUtils;
import com.originit.union.service.UserService;
import com.soecode.wxtools.exception.WxErrorException;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    WxBusiness wxBusiness;

    @Autowired
    UserService userService;
   /* @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysUserRoleService sysUserRoleService;*/

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

    /**
     * 从微信数据库中获取用户信息(包括用户的电话号码)的导入到数据库
     * @throws WxErrorException
     * @throws IOException
     */
    @RequestMapping("/userbind")
    @ResponseBody
    public  void userBind() throws WxErrorException, IOException {
        // 1 获取所有的openid,验证数据的有效性，然后将数据库中没有的插入数据库，比如这里，你微信都没有用户，那后面的操作都没有意义
        List<String> openIds = wxBusiness.getAllUserOpenIds();
        if (openIds.isEmpty()) {
            return;
        }
        userService.addUserIfNotExist(openIds);
        // 2 从数据库中查询没有绑定手机号的用户的openid,判断是否为空,alt + enter 快速修复
        List<String> noPhoneUserOpenIdList = userService.getOpenidListWithoutPhone();
        System.out.println("noPhoneUserOpenIdList数量"+noPhoneUserOpenIdList.size());
        if (noPhoneUserOpenIdList.isEmpty()) {
            return;
        }
        // 3. 通过获取到的openid去查询会员的电话,校验是否为空，如果为空就不用继续了，这些判断都是逻辑相关的
        List<UserBindDto> userBindDtos = wxBusiness.getUserBindDtos(noPhoneUserOpenIdList);
        System.out.println("userBindDtos数量"+userBindDtos.size());
        if (userBindDtos.isEmpty()) {
            return;
        }
        // 4. 更新用户的电话号码
        userService.updateUserBind(userBindDtos);
        // 你得先把逻辑理清楚，然后再去做，比如你定义了上面的4步，
        // 然后你每一步需要干嘛你就要去定义方法（为了让代码清晰，不然所有的if、for等等逻辑判断都在这就乱七八遭）
        // 现在这样即使是不懂得人也能大概看懂在干嘛
        // 然后因为涉及下层逻辑，你需要定义下层的接口，你就要思考自己到底需要下层为你干什么，比如第一步，将数据库中不存在的用户加入数据库
        // 定义了接口方法你不能直接调用，你可以使用mockito
    }

    public List<String> testMock () {
        return userService.getOpenidListWithoutPhone();
    }

}