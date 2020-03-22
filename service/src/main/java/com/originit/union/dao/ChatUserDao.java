package com.originit.union.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.originit.common.page.Pager;
import com.originit.union.entity.ChatUserEntity;
import com.originit.union.entity.vo.ChatUserVO;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatUserDao extends BaseMapper<ChatUserEntity> {
}
