package com.originit.union.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.originit.union.entity.UserBindEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserDao extends BaseMapper<UserBindEntity> {

    /**
     * 如果用户不存在插入用户到数据表
     * @param userList 用户列表
     * @return 插入的数量
     */
    int insertUsersIfNotExist (@Param("userList") List<UserBindEntity> userList);
}
