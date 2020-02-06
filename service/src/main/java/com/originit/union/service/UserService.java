package com.originit.union.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.originit.union.entity.UserBindEntity;
import com.originit.union.mapper.UserDao;

import java.util.List;

/**
 * 访问用户相关表的Service
 */
public interface UserService extends IService<UserBindEntity> {
    //更新会员手机绑定信息
    public  String updateUserBind(List<UserBindEntity> userBindEntityList);
}
