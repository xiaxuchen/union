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

    /**
     * 获取标签列表，同时获取用户数
     * @return
     */
    List<TagInfoVO> getAllTagWithCount();

    /**
     * 创建标签
     * @param tagName 标签名称
     * @return 新增的标签的信息
     */
    TagInfoVO createTag(String tagName);

    /**
     * 删除标签
     * @param tagId 标签的id
     */
    void deleteTag(String tagId);

    /**
     * 更新标签名称
     * @param tagId 标签id
     * @param tagName 标签名称
     */
    void updateTag(Long tagId, String tagName);
}
