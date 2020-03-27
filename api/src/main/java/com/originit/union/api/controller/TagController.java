package com.originit.union.api.controller;

import com.originit.common.exceptions.ParameterInvalidException;
import com.originit.common.util.POIUtil;
import com.originit.common.util.StringUtil;
import com.originit.union.dao.UserDao;
import com.originit.union.entity.dto.TagUserAddDto;
import com.originit.union.entity.vo.TagInfoVO;
import com.originit.union.service.TagService;
import com.xxc.response.anotation.ResponseResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
    @DeleteMapping("/{tagId}")
    public void deleteTag (@PathVariable String tagId) {
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

    /**
     * 添加用户标签
     * @param id 用户id
     * @param tagId  标签id
     */
    @PostMapping("/user")
    public void userAddTag (@RequestParam Long id,@RequestParam Long tagId) {
        tagService.addTagOfUser(Arrays.asList(new TagUserAddDto(id,null)), tagId);
    }

    @DeleteMapping("/user/{userTagId}")
    public void userDeleteTag (@PathVariable Long userTagId) {
        tagService.deleteTagOfUser(userTagId);
    }

    /**
     * 通过文件导入
     * @param file 用户id或用户openId的文件
     * @param tagId 标签的id
     */
    @PostMapping("/user/list")
    public void importTagOfUser (MultipartFile file,@RequestParam Long tagId) throws IOException {
        final List<TagUserAddDto> tagUserAddDtos = POIUtil.customQuery(file.getInputStream(), file.getOriginalFilename(), workbook -> {
            List<TagUserAddDto> list = new ArrayList<>();
            Sheet sheet = workbook.getSheetAt(0);
            // 从第二行开始
            for (int row = 1; row <= sheet.getLastRowNum(); row++) {
                Row rowObj = sheet.getRow(row);
                Long id = null;
                final String idCellValue = POIUtil.getCellValue(rowObj.getCell(1));
                // 校验
                if (idCellValue != null && !StringUtils.isBlank(idCellValue)) {
                    try {
                        id = Long.parseLong(idCellValue);
                    }catch (NumberFormatException e) {
                        throw new ParameterInvalidException("用户编号必须为数字");
                    }
                }
                String phone = POIUtil.getCellValue(rowObj.getCell(0));
                if (id == null && (phone == null || StringUtils.isBlank(phone))) {
                    continue;
                }
                list.add(new TagUserAddDto(id,
                        POIUtil.getCellValue(rowObj.getCell(0))));
            }
            return list;
        });
        tagService.addTagOfUser(tagUserAddDtos,tagId);
    }
}
