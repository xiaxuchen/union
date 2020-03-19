package com.originit.union.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.originit.union.entity.MessageEntity;
import org.springframework.stereotype.Repository;

/**
 * 访问message表的dao
 */
@Repository
public interface MessageDao extends BaseMapper<MessageEntity> {
}
