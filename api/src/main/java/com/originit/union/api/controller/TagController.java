package com.originit.union.api.controller;

import com.originit.union.entity.vo.TagInfoVO;
import com.originit.union.service.TagService;
import com.xxc.response.anotation.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author xxc、
 */
@RestController
@RequestMapping("/tag")
@ResponseResult
public class TagController {

    private TagService tagService;

    @Autowired
    public void setTagService(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/list")
    public List<TagInfoVO> getTagList () {
        return tagService.getAllTagWithCount();
    }

    /**
     * 创建标签
     * @param tagName
     * @return
     */
    @PostMapping
    public TagInfoVO createTag (@RequestParam String tagName) {
        return tagService.createTag(tagName);
    }

    /**
     * 删除标签
     * @param tagId 标签id
     */
    @DeleteMapping
    public void deleteTag (@RequestParam String tagId) {
        tagService.deleteTag(tagId);
    }

    /**
     * 修改标签名
     * @param tagName 标签名称
     */
    @PutMapping
    public void editTag (@RequestParam Long tagId, @RequestParam String tagName) {
        tagService.updateTag (tagId,tagName);
    }
}
