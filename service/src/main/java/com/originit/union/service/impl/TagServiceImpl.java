package com.originit.union.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.originit.union.bussiness.TagBusiness;
import com.originit.union.entity.TagEntity;
import com.originit.union.entity.converter.TagConverter;
import com.originit.union.entity.vo.TagInfoVO;
import com.originit.union.dao.TagDao;
import com.originit.union.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 标签服务
 * @author xxc、
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagDao, TagEntity> implements TagService {

    TagBusiness tagBusiness;

    @Autowired
    public void setTagBusiness(TagBusiness tagBusiness) {
        this.tagBusiness = tagBusiness;
    }

    @Override
    public List<TagInfoVO> getTagList() {
        return TagConverter.INSTANCE.to(baseMapper.selectList(null));
    }

    @Override
    public List<TagInfoVO> getAllTagWithCount() {
        return baseMapper.selectAllWithCount();
    }

    @Override
    public TagInfoVO createTag(String tagName) {
        final int wechatId = tagBusiness.createTag(tagName);
        final TagEntity tag = new TagEntity();
        tag.setName(tagName);
        tag.setWechatTagId(wechatId);
        try {
            baseMapper.insert(tag);
        } catch (Exception e) {
            // 出错了就删除
            tagBusiness.deleteTag(wechatId);
        }
        final TagInfoVO tagInfoVO = new TagInfoVO();
        tagInfoVO.setId(tag.getId());
        tagInfoVO.setName(tag.getName());
        tagInfoVO.setCount(0);
        return tagInfoVO;
    }

    @Override
    public void deleteTag(String tagId) {
        final Integer wechatTagId = baseMapper.selectOne(new QueryWrapper<TagEntity>()
                .lambda().select(TagEntity::getWechatTagId)
                .eq(TagEntity::getId, tagId)).getWechatTagId();
        tagBusiness.deleteTag(wechatTagId);
        baseMapper.deleteById(tagId);
    }

    @Override
    public void updateTag(Long tagId, String tagName) {
        final Integer wechatTagId = baseMapper.selectOne(new QueryWrapper<TagEntity>()
                .lambda().select(TagEntity::getWechatTagId)
                .eq(TagEntity::getId, tagId)).getWechatTagId();
        tagBusiness.updateTag (wechatTagId, tagName);
        baseMapper.update(null,new UpdateWrapper<TagEntity>().lambda()
        .set(TagEntity::getName,tagName)
                .eq(TagEntity::getId,tagId));
    }
}
