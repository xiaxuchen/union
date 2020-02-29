package com.originit.union.api.controller;

import com.google.common.primitives.Bytes;
import com.mchange.lang.ByteUtils;
import com.originit.common.exceptions.ParameterInvalidException;
import com.originit.common.util.FileUDUtil;
import com.xxc.response.anotation.OriginResponse;
import com.xxc.response.anotation.ResponseResult;
import org.apache.commons.io.FileUtils;
import org.apache.coyote.http2.ByteUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @author xxc、
 */
@RestController
@ResponseResult
@RequestMapping("/resource")
public class ResourceController {

    /**
     * 上传图片返回图片的key和url
     * @param file 图片文件
     * @return 图片的信息
     */
    @PostMapping("/file")
    public String uploadFile (MultipartFile file) {
        try {
            return FileUDUtil.uploadFile(file.getInputStream(), file.getOriginalFilename());
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
}
