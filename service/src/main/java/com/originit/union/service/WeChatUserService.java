package com.originit.union.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.originit.common.page.Pager;
import com.originit.union.entity.UserBindEntity;
import com.originit.union.entity.vo.UserInfoVO;

import java.util.List;
/**
 * 访问用户相关表的Service
 * @author  xxc、
 */

public interface WeChatUserService extends IService<UserBindEntity> {

    /**
     * 通过openId去获取用户的信息
     * @param openId 用户的openId
     * @return 用户的信息
     */
    UserBindEntity getUserInfoByOpenId (String openId);

    /**
     * 添加或更新用户列表
     * @param users 用户列表
     */
    void addOrUpdateUsers(List<UserBindEntity> users);

    /**
     * 获取用户列表信息
     * @param searchKey 搜索的关键字
     * @param tagList 标签列表
     * @param curPage 当前页
     * @param pageSize 每页大小
     * @return 分页的用户信息列表
     */
    Pager<UserInfoVO> getUserInfoList(String searchKey, List<Integer> tagList, int curPage, int pageSize);

    /**
     * 导入用户信息到系统中
     */
    void importUsers();

    /**
     * 获取系统中用户总数以及有电话号码的用户的总数 * @return [0]用户总数，[1]用户绑定数
     */
    Integer[] getUserStatistic();

    /**
     * 根据电话号码获取用户信息
     * @param phones 电话
     * @return 用户信息
     */
    List<UserInfoVO> getUserInfoByPhones(List<String> phones);
}
