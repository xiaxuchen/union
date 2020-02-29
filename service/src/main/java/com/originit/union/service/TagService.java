package com.originit.union.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.originit.union.entity.TagEntity;
import com.originit.union.entity.vo.TagInfoVO;

import java.util.List;

public interface TagService extends IService<TagEntity> {

    /**
     * 获取标记列表
     * @return
     */
    List<TagInfoVO> getTagList();
}
