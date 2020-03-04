package com.originit.union.api.controller;

import com.originit.common.page.Pager;
import com.originit.union.api.util.SHA256Util;
import com.originit.union.api.util.ShiroUtils;
import com.originit.union.entity.dto.SysUserDto;
import com.originit.union.entity.dto.SysUserQueryDto;
import com.originit.union.entity.dto.SysUserUpdateDto;
import com.originit.union.entity.vo.LoginUserVO;
import com.originit.union.entity.vo.RoleVO;
import com.originit.union.entity.vo.SysUserVO;
import com.originit.union.service.SysRoleService;
import com.originit.union.service.SysUserService;
import com.xxc.response.anotation.ResponseResult;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xxc、
 */
@RestController
@RequestMapping("/sysuser")
@ResponseResult
public class UserController {

    public static final String TOKEN = "token";
    private SysUserService sysUserService;

    private SysRoleService sysRoleService;

    @Autowired
    public void setSysUserService(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    @Autowired
    public void setSysRoleService(SysRoleService sysRoleService) {
        this.sysRoleService = sysRoleService;
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
        ShiroUtils.deleteCache(ShiroUtils.getUserInfo().getUserId(),true);
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
        // 删除用户
        sysUserService.removeByIds(Arrays.asList(ids));
    }

    @PutMapping
    public void updateSysUser (@RequestBody SysUserUpdateDto sysUserDto) {
        String salt = ByteSource.Util.bytes(sysUserDto.getUsername()).toString();
        if (sysUserDto.getPassword() != null) {
            sysUserDto.setSalt(salt);
            sysUserDto.setPassword(SHA256Util.sha256(sysUserDto.getPassword(),salt));
        }
        sysUserService.updateSysUser(sysUserDto);
    }

    /**
     * 获取所有的角色信息
     * @return
     */
    @GetMapping("/roles")
    public List<RoleVO> getRoles () {
        return sysRoleService.list().stream().map(sysRoleEntity -> new RoleVO(sysRoleEntity.getRoleId(), sysRoleEntity.getRoleName())).collect(Collectors.toList());
    }
}