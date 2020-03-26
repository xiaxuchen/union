package com.originit.union.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.originit.union.entity.TagEntity;
import com.originit.union.entity.vo.TagInfoVO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author xxc、
 */
@Repository
public interface TagDao extends BaseMapper<TagEntity> {

    /**
     * 获取所有标签的信息以及标签的数量
     * @return 标签的信息
     */
    List<TagInfoVO> selectAllWithCount();
}
