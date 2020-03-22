package com.originit.union.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.originit.union.entity.UserBindEntity;
import com.originit.union.entity.domain.UserInfo;
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
     * @param phoneList 电话列表
     * @param tagList 标签列表
     * @return 用户信息分页
     */
    IPage<UserInfo> selectUserByPhonesAndTags(Page<?> page, @Param("phoneList") List<String> phoneList,
                                              @Param("tagList") List<Integer> tagList);
}
