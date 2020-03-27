package com.originit.union.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.originit.union.entity.UserBindEntity;
import com.originit.union.entity.domain.UserInfo;
import com.originit.union.entity.dto.GetChatUserDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author xxc、
 */
@Repository
public interface UserDao extends BaseMapper<UserBindEntity> {

    /**
     * 如果用户不存在插入用户到数据表
     * @param user 用户信息
     * @return 插入的数量
     */
    int insertOrUpdateUser(UserBindEntity user);


    /**
     * 通过电话号码和用户标签
     * @param searchKey 搜索关键字
     * @param tagList 标签列表
     * @return 用户信息分页
     */
    IPage<UserInfo> searchUsers(Page<?> page, @Param("searchKey") String searchKey,
                                @Param("tagList") List<Integer> tagList);

    /**
     * 根据用户的电话获取用户信息
     * @param phones 用户的电话
     * @return 用户的信息
     */
    List<UserInfo> selectUserByPhones(@Param("phone") List<String> phones);

    /**
     * 查询可被接入的用户的信息
     * @param page 分页
     * @param query 搜索对象
     * @return
     */
    IPage<UserBindEntity> selectReceivableUsers(Page<Object> page,@Param("query") GetChatUserDto query);

    /**
     * 查找改用户是否可以接入(48小时内交互过，同时没有其他经理接入)
     * @param openId 用户id
     * @return 是否可接入
     */
    Boolean selectCanReceive(String openId);
}
