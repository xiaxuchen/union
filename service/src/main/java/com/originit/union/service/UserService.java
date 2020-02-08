package com.originit.union.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.originit.union.entity.UserBindEntity;
import com.originit.union.entity.dto.UserBindDto;
import com.originit.union.mapper.UserDao;

import java.util.List;
/**
 * @Description 访问用户相关表的Service
 * @Author
 * @CreateTime
 */

public interface UserService extends IService<UserBindEntity> {
    /**
     * 添加用户openid信息
     * @param openidlist 将传入的openId列表插入用户绑定表中，如果openId在数据库已存在则不插入
     * @return
     */
      void addUserIfNotExist(List<String> openidlist);

    /**
     *
     * @return List<String>   获取列表中phone为空值的openid
     */
      List<String> getOpenidListWithoutPhone();

    /**
     *
     * @param userBindDtoList 根据getOpenidListWithoutPhone更新用户绑定的的phone信息
     */
      void updateUserBind(List<UserBindDto> userBindDtoList);
    /**
     * 根据用户的电话列表获取用户openid
     * @param phonelist  电话列表
     * @return   openid列表
     */
    List<String> getUseridByphone(List<String> phonelist);

    /**
     * 获取所有的会员信息
     * @return 会员信息列表
     */
    List<UserBindDto> getAllUserBindInfo();
}
