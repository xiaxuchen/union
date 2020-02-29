package com.originit.union.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.originit.common.page.Pager;
import com.originit.union.entity.UserBindEntity;
import com.originit.union.entity.dto.SysUserDto;
import com.originit.union.entity.vo.UserInfoVO;

import java.util.List;
/**
 * 访问用户相关表的Service
 * @author  xxc、
 */

public interface UserService extends IService<UserBindEntity> {

    /**
     * 添加或更新用户列表
     * @param users 用户列表
     */
    void addOrUpdateUsers(List<UserBindEntity> users);

    /**
     * 获取用户列表信息
     * @param phone 用户的电话
     * @param tagList 标签列表
     * @param curPage 当前页
     * @param pageSize 每页大小
     * @return 分页的用户信息列表
     */
    Pager<UserInfoVO> getUserInfoList(List<String> phone, List<Integer> tagList, int curPage, int pageSize);

}
