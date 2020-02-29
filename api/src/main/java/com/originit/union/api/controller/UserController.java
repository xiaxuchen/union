package com.originit.union.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.originit.common.page.Pager;
import com.originit.union.api.util.SHA256Util;
import com.originit.union.api.util.ShiroUtils;
import com.originit.union.entity.AgentInfoEntity;
import com.originit.union.entity.SysUserRoleEntity;
import com.originit.union.entity.dto.SysUserDto;
import com.originit.union.entity.dto.SysUserQueryDto;
import com.originit.union.entity.vo.LoginUserVO;
import com.originit.union.entity.vo.SysUserVO;
import com.originit.union.service.AgentInfoService;
import com.originit.union.service.SysUserRoleService;
import com.originit.union.service.SysUserService;
import com.originit.union.service.UserService;
import com.xxc.response.anotation.ResponseResult;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author xxc、
 */
@RestController
@RequestMapping("/sysuser")
@ResponseResult
public class UserController {

    public static final String TOKEN = "token";
    private SysUserService sysUserService;

    private SysUserRoleService sysUserRoleService;

    private AgentInfoService agentInfoService;

    @Autowired
    public void setAgentInfoService(AgentInfoService agentInfoService) {
        this.agentInfoService = agentInfoService;
    }

    @Autowired
    public void setSysUserRoleService(SysUserRoleService sysUserRoleService) {
        this.sysUserRoleService = sysUserRoleService;
    }

    @Autowired
    public void setSysUserService(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    /**
     * 登录
     */
    @RequestMapping("/login")
    @ResponseBody
    public LoginUserVO login(@RequestParam String username, @RequestParam String password,
                             HttpServletRequest request, HttpServletResponse response){
        //验证身份和登陆
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username,password);
        /* 进行登录操作 */
        subject.login(token);
        response.setHeader(TOKEN, request.getSession().getId());
        return new LoginUserVO(ShiroUtils.getUserInfo().getUsername(),ShiroUtils.getUserInfo().getHeadImg());
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public void logout () {
        SecurityUtils.getSubject().logout();
    }

    /**
     * 是否允许跳转
     * @param url 请求路径
     * @return 是否有权限跳转
     */
    @GetMapping("/permit")
    public Boolean permit (String url) {
        return true;
    }


    /**
     * 添加系统用户，若为客户经理则添加至客户经理表中
     * @param sysUserDto 用户信息
     * @return 用户id
     */
    @PostMapping
    public Long addSysUser (@RequestBody SysUserDto sysUserDto) {
        String salt = ByteSource.Util.bytes(sysUserDto.getUsername()).toString();
        sysUserDto.setSlat(salt);
        sysUserDto.setPassword(SHA256Util.sha256(sysUserDto.getPassword(),salt));
        return sysUserService.addSysUser(sysUserDto);
    }

    /**
     * 获取用户列表
     * @return
     */
    @GetMapping("/list")
    public Pager<SysUserVO> getSysUserList (SysUserQueryDto queryDto) {
        return sysUserService.search(queryDto);
    }

    @DeleteMapping("/{id}")
    public void deleteSysUser (@PathVariable("id") Long[] ids) {
        for (Long id : ids) {
            // 删除一个系统用户需要删除其用户信息、角色信息、客户经理信息
            sysUserService.removeById(id);
            sysUserRoleService.remove(new QueryWrapper<SysUserRoleEntity>().lambda().eq(SysUserRoleEntity::getUserId,id));
            agentInfoService.remove(new QueryWrapper<AgentInfoEntity>().lambda().eq(AgentInfoEntity::getSysUserId,id));
        }
    }
}