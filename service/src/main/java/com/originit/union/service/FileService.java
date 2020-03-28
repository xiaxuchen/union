package com.originit.union.service;

import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.service.IService;
import com.originit.union.entity.FileRefEntity;
import com.originit.union.exception.file.CodeNotExistException;
import com.originit.union.exception.file.FileException;
import com.originit.union.exception.file.FileNotFoundException;

import java.io.File;
import java.io.InputStream;

/**
 * 文件服务
 * @author xxc、
 */
public interface FileService extends IService<FileRefEntity> {

    /**
     * 默认超时时间，15分钟
     */
    long DEFAULT_TIME_OUT = 15 * 60;

    /**
     * 带有默认过期时间的保存文件的方法，默认为{@link DEFAULT_TIME_OUT}
     * @param inputStream 文件输入流
     * @param fileName 文件名称
     * @return 文件的code
     */
    default String saveFile (InputStream inputStream,String fileName) {
        return saveFile(inputStream,fileName,DEFAULT_TIME_OUT);
    }
    /**
     * 保存文件
     * @param inputStream 文件输入流
     * @param fileName 文件名
     * @param expire 过期时间
     * @return 返回生成的code
     * @throws FileException 若io错误抛出
     */
    String saveFile(InputStream inputStream,String fileName,long expire) throws FileException;

    /**
     * 当文件的引用数减少为0时删除
     * @param code 文件的code
     * @throws CodeNotExistException code编码不存在的情况下抛出
     * @throws FileNotFoundException 文件不存在的情况下抛出
     */
    default void deleteFile (String code) throws FileNotFoundException, CodeNotExistException {
        deleteFile(code,false);
    }


    /**
     * 当文件的引用数减少为0时删除，若开启强制删除则不管引用数
     * @param code 文件的code
     * @param focus 是否强制删除
     * @throws CodeNotExistException code编码不存在的情况下抛出
     * @throws FileNotFoundException 文件不存在的情况下抛出
     */
    void deleteFile (String code,Boolean focus) throws FileNotFoundException, CodeNotExistException;

    /**
     * 获取文件
     * @param code 文件的编码
     * @throws CodeNotExistException code编码不存在的情况下抛出
     * @throws FileNotFoundException 文件不存在的情况下抛出
     */
    File getFile (String code) throws FileNotFoundException, CodeNotExistException;
    /**
     * 使用文件
     * @param code 文件的编码
     * @throws CodeNotExistException code编码不存在的情况下抛出
     * @throws FileNotFoundException 文件不存在的情况下抛出
     */
    void applyFile (String code) throws FileNotFoundException, CodeNotExistException;

    /**
     * 清理掉超时的无效文件
     */
    void clearTimeoutFile ();

    /**
     * 是否存在该文件
     * @param code 文件编码
     * @return
     */
    boolean exists(String code);
}
