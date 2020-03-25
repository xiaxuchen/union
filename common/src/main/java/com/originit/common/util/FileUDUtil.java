package com.originit.common.util;

import com.originit.common.exceptions.InternalServerException;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

public class FileUDUtil {
    public static final String PATH = "\\images";

    /**
     * 处理下载文件时的文件名中文乱码问题，兼容浏览器
     * @param filename 源文件名
     * @param agent 浏览器的user-agent
     */
    private static String resolveDownloadFileName(String filename, String agent){
        String filenameEncoder = null;
        try {
            if(agent.contains("MSIE")) {
                // IE浏览器
                filenameEncoder = URLEncoder.encode(filename, "utf-8");
                filenameEncoder = filenameEncoder.replace("+", " ");
            }
            else if (agent.contains("Firefox")) {
                // 火狐浏览器
                BASE64Encoder base64Encoder = new BASE64Encoder();
                filenameEncoder = "=?utf-8?B?" + base64Encoder.encode(filename.getBytes(Charset.forName("utf-8"))) + "?=";
            }
            else { // 其它浏览器
                try {
                    filenameEncoder = URLEncoder.encode(filename, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            throw new InternalServerException("文件名编码异常");
        }
        return filenameEncoder;
    }

    /**
     * 将path的文件转换为ResponseEntity
     * @param code 文件所在文件夹路径
     * @param filename 文件名
     * @param agent 浏览器的user-agent
     */
    public static void downloadFile(String code, String filename, String agent, HttpServletResponse resp) {
        String realPath = new String(Base64.getDecoder().decode(code), StandardCharsets.UTF_8);
        if (filename == null) {
            filename = realPath.substring(realPath.lastIndexOf("\\") + 1);
        }
        downloadFileWithPath("",realPath,filename,agent,resp);
    }

    /**
     * 获取对应code的文件
     * @param code 上传文件生成的code
     */
    public static File getFile (String code) {
        if (code == null) {
            return null;
        }
        String realPath = new String(Base64.getDecoder().decode(code), StandardCharsets.UTF_8);
        // 获取目录下的资源
        return new File(FileUDUtil.class.getClassLoader().getResource("").getPath(), realPath);
    }

    public static void downloadFileWithPath (String relativePath,String realPath, String filename, String agent, HttpServletResponse resp) {
        try {
           // 获取目录下的资源
            File file = new File(FileUDUtil.class.getClassLoader().getResource(relativePath).getPath(), realPath);
            resp.reset();
//          // 让浏览器显示下载文件对话框
            resp.setContentType(MediaType.parseMediaType(Files.probeContentType(Paths.get(file.getAbsolutePath()))).getType());
            resp.setCharacterEncoding("utf-8");
            resp.setContentLength((int) file.length());
            resp.setHeader("Content-Disposition", "attachment;filename="+resolveDownloadFileName(filename,agent));
            byte[] buff = new byte[1024];
            BufferedInputStream bis = null;
            OutputStream os = null;
            try {
                os = resp.getOutputStream();
                bis = new BufferedInputStream(new FileInputStream(file));
                int i = 0;
                while ((i = bis.read(buff)) != -1) {
                    os.write(buff, 0, i);
                    os.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerException("文件下载异常");
        }
    }

    /**
     * 获取默认的头像
     * @return 默认头像
     */
    public static File getDefaultHeadImg () {
        return new File(FileUDUtil.class.getClassLoader().getResource("images").getPath(), "/default_logo.jpg");
    }


    /**
     * 通过文件名获取hash
     * @param filename 文件名
     * @return 路径
     */
    private static String getPath (String filename) {
        //得到hashCode
        int hashcode = filename.hashCode();
        //得到名为1到16的下及文件夹
        int dir1 = hashcode & 0xf;
        //得到名为1到16的下下及文件夹
        int dir2 = (hashcode & 0xf0) >> 4;
        //得到文件路径
        String dir = PATH + "\\" + dir1 + "\\" + dir2 + "\\" +
                UUID.randomUUID().toString().replace("-","") +
                filename.substring(filename.lastIndexOf("."));
        return dir;
    }
    /***
     * 上传文件
     * @param inputStream 文件的输入流
     * @param filename 文件名
     * @return 文件的路径
     */
    public static String uploadFile(InputStream inputStream,String filename) {
        try {
            String path = getPath(filename);
            mkdirIfNotExist(path);
            File file = new File(FileUDUtil.class.getClassLoader().getResource("").getPath(), path);
            FileUtils.copyInputStreamToFile(inputStream,file);
            // 返回base64编码
            return  Base64.getEncoder().encodeToString(path.getBytes(StandardCharsets.UTF_8));
        }catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerException("文件上传异常");
        }
    }

    /**
     * 创建文件夹
     * @param path 路径
     */
    private static void mkdirIfNotExist(String path) {
        File file = new File(FileUDUtil.class.getClassLoader().getResource("").getPath(), path.substring(0,path.lastIndexOf("\\")));
        if (!file.exists()) {
            file.mkdirs();
        }
    }
}