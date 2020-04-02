package com.originit.union.api.controller;

import com.originit.common.config.RedisConfig;
import com.originit.common.exceptions.ParameterInvalidException;
import com.originit.common.exceptions.UserNotLoginException;
import com.originit.common.page.Pager;
import com.originit.common.util.POIUtil;
import com.originit.common.util.RedisCacheProvider;
import com.originit.common.util.SHA256Util;
import com.originit.union.api.shiro.config.ShiroConfig;
import com.originit.union.api.util.ShiroUtils;
import com.originit.union.entity.AgentInfoEntity;
import com.originit.union.entity.SysUserEntity;
import com.originit.union.entity.dto.SysUserCreateDto;
import com.originit.union.entity.dto.SysUserQueryDto;
import com.originit.union.entity.dto.SysUserUpdateDto;
import com.originit.union.entity.vo.LoginUserVO;
import com.originit.union.entity.vo.RoleVO;
import com.originit.union.entity.vo.SysUserVO;
import com.originit.union.entity.vo.UserInfoVO;
import com.originit.union.exception.file.FileException;
import com.originit.union.service.*;
import com.originit.union.util.ExcelParseUtil;
import com.xxc.response.anotation.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author xxc、
 */
@RestController
@RequestMapping("/sysuser")
@ResponseResult
@Slf4j
public class UserController {

    public static final String TOKEN = "token";
    private SysUserService sysUserService;

    private SysRoleService sysRoleService;

    private RedisCacheProvider redisCacheProvider;

    private AgentInfoService agentInfoService;

    private UserAgentService userAgentService;

    @Autowired
    public void setUserAgentService(UserAgentService userAgentService) {
        this.userAgentService = userAgentService;
    }

    @Autowired
    public void setAgentInfoService(AgentInfoService agentInfoService) {
        this.agentInfoService = agentInfoService;
    }


    @Autowired
    public void setRedisCacheProvider(RedisCacheProvider redisCacheProvider) {
        this.redisCacheProvider = redisCacheProvider;
    }

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
        SysUserEntity userInfo = ShiroUtils.getUserInfo();
        // 更新用户和session的关系
        final String userKey = ShiroUtils.generateUserKey(userInfo.getUserId());
        redisCacheProvider.set(userKey,ShiroUtils.getSession().getId(), ShiroConfig.EXPIRE);
        return obtainLoginUser(userInfo.getUserId());
    }

    /**
     * 获取登录用户信息
     * @param userId 用户id
     * @return
     */
    private LoginUserVO obtainLoginUser (Long userId) {
        SysUserEntity userInfo = sysUserService.getById(userId);
        LoginUserVO loginUserVO = LoginUserVO.builder()
                .id(userInfo.getUserId())
                .username(userInfo.getUsername())
                .headImg(userInfo.getHeadImg())
                .phone(userInfo.getPhone())
                .build();
        AgentInfoEntity info = agentInfoService.getByUserId(userId);
        if (info == null) {
            loginUserVO.setIsAgent(false);
        } else {
            loginUserVO.setIsAgent(true);
            loginUserVO.setDes(info.getDes());
            loginUserVO.setName(info.getName());
            loginUserVO.setSex(info.getSex());
        }
        return loginUserVO;
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public void logout () {
        final SysUserEntity userInfo = ShiroUtils.getUserInfo();
        SecurityUtils.getSubject().logout();
        if (userInfo != null) {
            ShiroUtils.deleteCache(userInfo.getUserId(),true);
        }
        log.info("用户登出成功");
    }

    /**
     * 是否允许跳转
     * @param url 请求路径
     * @return 是否有权限跳转
     */
    @GetMapping("/permit")
    public Boolean permit (@RequestParam String url) {
        return true;
    }


    /**
     * 添加系统用户，若为客户经理则添加至客户经理表中
     * @param sysUserDto 用户信息
     * @return 用户id
     */
    @PostMapping
    public Long addSysUser (@RequestBody SysUserCreateDto sysUserDto) {
        String salt = ByteSource.Util.bytes(sysUserDto.getUsername()).toString();
        sysUserDto.setSalt(salt);
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
        // 如果需要修改密码，则设置加密的盐
        if (sysUserDto.getPassword() != null) {
            // 设置用户名为加密的盐
            String salt = ByteSource.Util.bytes(sysUserDto.getUsername()).toString();
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

    @GetMapping("/info")
    public LoginUserVO getUserInfo () {
        return obtainLoginUser(ShiroUtils.getUserInfo().getUserId());
    }

    /**
     * 修改密码
     * @param originPwd 原密码
     * @param newPwd 新密码
     * @return
     */
    @PutMapping("/pwd")
    public Boolean alterPassword (@RequestParam String originPwd,@RequestParam String newPwd){
        if (originPwd.length() < 8 || originPwd.length() > 32) {
            throw new ParameterInvalidException("密码应该在8-32位之间");
        }

        if (newPwd.length() < 8 || newPwd.length() > 32) {
            throw new ParameterInvalidException("密码应该在8-32位之间");
        }

        sysUserService.updatePwd(ShiroUtils.getUserInfo().getUserId(),originPwd,newPwd);
        return true;
    }

    /**
     * 导入经理和用户的绑定关系
     * @param file excel文件
     * @param mode 模式，0为覆盖写，1为正常写，如果已经在其他经理下，就提示
     * @return 返回覆盖或忽略的用户openId
     */
    @PostMapping("/belong")
    public List<String> importBindUser (MultipartFile file,@RequestParam Long agentId, @RequestParam(required = false) Integer mode) {
        final InputStream inputStream;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new FileException("上传的文件异常");
        }
        // 1. 解析电话号码
        List<String> phones = ExcelParseUtil.parsePhoneTemplate(inputStream,file.getOriginalFilename());
        // 2. 根据不同的模式去选择是覆盖写还是忽略写
        if (mode != null && mode.equals(0)) {
            return userAgentService.addRelationClearOld(agentId,phones);
        } else  {
            final Set<String> set = userAgentService.addRelation(agentId, phones);
            return set.stream().collect(Collectors.toList());
        }
    }

    /**
     * 获取所属的用户信息
     * @param curPage 当前页
     * @param pageSize 每页的大小
     * @return
     */
    @GetMapping("/belong")
    public Pager<UserInfoVO> getBelongUserInfo(@RequestParam Long agentId,@RequestParam(required = false) Integer curPage,
                                         @RequestParam(required = false) Integer pageSize) {
        if (curPage == null) {
            curPage = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        return userAgentService.pagerUserAgent(agentId,curPage,pageSize);
    }

    /**
     * 用户未登录，用于shiro拦截的重定向，从而通知客户端，否则返回状态码500
     */
    @RequestMapping("/notLogin")
    public void notLogin () {
        throw new UserNotLoginException();
    }
}