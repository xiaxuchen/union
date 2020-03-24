package com.originit.union.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.originit.union.entity.ChatUserAgentEntity;
import com.originit.union.entity.UserBindEntity;
import com.originit.union.entity.vo.ChatUserVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author xxc、
 */
@Repository
public interface ChatUserAgentDao extends BaseMapper<ChatUserAgentEntity> {

    /**
     * 获取经理的所有用户信息
     * @param page 分页
     * @return 经理的所有聊天的用户信息
     */
    List<UserBindEntity> selectAgentUsers(@Param("userId") Long userId);
}
