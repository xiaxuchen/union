package com.originit.union.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.originit.common.page.Pager;
import com.originit.union.entity.ChatUserEntity;
import com.originit.union.entity.UserBindEntity;
import com.originit.union.entity.dto.GetChatUserDto;
import com.originit.union.entity.vo.ChatUserVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatUserDao extends BaseMapper<ChatUserEntity> {
    /**
     * 获取等待用户的用户信息
     * @param page 分页
     * @return 分页的用户信息
     */
    IPage<UserBindEntity> selectWaitingUsers(Page<ChatUserEntity> page,@Param("query") GetChatUserDto query);
}
