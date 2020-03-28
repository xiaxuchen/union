package com.originit.union.api.controller;

import com.originit.common.exceptions.DataNotFoundException;
import com.originit.common.exceptions.ParameterInvalidException;
import com.originit.common.util.FileUDUtil;
import com.originit.union.api.annotation.Anon;
import com.originit.union.service.FileService;
import com.xxc.response.anotation.OriginResponse;
import com.xxc.response.anotation.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xxc、
 */
@RestController
@ResponseResult
@RequestMapping("/resource")
@Anon
public class ResourceController {

    private static final Map<String,String> templateMap;

    private FileService fileService;

    @Autowired
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    static {
        templateMap = new HashMap<>();
        templateMap.put("phone", "/phone_template.xls");
        templateMap.put("tag","/tag_template.xls");
    }

    /**
     * 上传图片返回图片编码
     * @param file 图片文件
     * @return 图片的编码
     */
    @PostMapping("/file")
    public String uploadFile (MultipartFile file) {
        try {
            return fileService.saveFile(file.getInputStream(), file.getOriginalFilename());
        } catch (IOException e) {
            e.printStackTrace();
            throw new ParameterInvalidException("文件上传异常");
        }
    }

    /**
     * 获取图片的文件
     * @param code 图片码
     * @return 文件
     */
    @GetMapping("/file/{code}")
    @OriginResponse
    public void getFile (@PathVariable String code, HttpServletRequest request, HttpServletResponse resp) throws IOException {
        FileUDUtil.downloadFile(code, null, request.getHeader("User-Agent"),resp);
    }

    /**
     * 获取电话模板文件
     * @return 文件
     */
    @GetMapping("/template/{templateKey}")
    @OriginResponse
    public void getPhoneTemplateFile (@PathVariable String templateKey, HttpServletRequest request, HttpServletResponse resp) throws IOException {
        final String templateName = templateMap.get(templateKey);
        if (templateName == null) {
            throw new DataNotFoundException("找不到文件");
        }
        FileUDUtil.downloadFileWithPath("files",templateName, "模板.xls", request.getHeader("User-Agent"),resp);
    }

}
