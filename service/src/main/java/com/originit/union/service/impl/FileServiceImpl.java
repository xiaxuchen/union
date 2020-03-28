package com.originit.union.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.originit.common.util.ExceptionUtil;
import com.originit.common.util.FileUDUtil;
import com.originit.union.dao.FileRefDao;
import com.originit.union.entity.FileRefEntity;
import com.originit.union.exception.file.CodeNotExistException;
import com.originit.union.exception.file.FileException;
import com.originit.union.exception.file.FileNotFoundException;
import com.originit.union.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.apache.bcel.classfile.Code;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FileServiceImpl extends ServiceImpl<FileRefDao, FileRefEntity> implements FileService {
    @Override
    public String saveFile(InputStream inputStream, String fileName, long expire) throws FileException {
        try {
            final String code = FileUDUtil.saveFile(inputStream, fileName);
            save(FileRefEntity.builder()
                    .code(code)
                    .count(0)
                    .expire(expire)
                    .build());
            return code;
        }catch (Exception e) {
            throw new FileException(e,"文件保存失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFile(String code, Boolean focus) throws FileNotFoundException, CodeNotExistException {
        final int count = baseMapper.decreCount(code);
        if (count == 0) {
            throw new  CodeNotExistException();
        }
        final LambdaQueryWrapper<FileRefEntity> qw = new QueryWrapper<FileRefEntity>().lambda().eq(FileRefEntity::getCode, code);
        // 如果不是强制只有数量为0才删除
        if (!focus) {
            qw.eq(FileRefEntity::getCount,0);
        }
        baseMapper.delete(qw);
    }

    @Override
    public File getFile(String code) throws FileNotFoundException, CodeNotExistException {
        final int count = count(new QueryWrapper<FileRefEntity>().lambda().eq(FileRefEntity::getCode, code));
        // 如果查不到就没有
        if (count == 0) {
            throw new CodeNotExistException();
        }
        try {
            final File file = FileUDUtil.getFile(code);
            return file;
        }catch (Exception e){
            throw new FileNotFoundException(e,"文件找不到");
        }
    }

    @Override
    public void applyFile(String code) throws  CodeNotExistException {
        int count = baseMapper.increCount(code);
        if (count == 0) {
            throw new CodeNotExistException();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearTimeoutFile() {
        // 获取到超时的code
        List<FileRefEntity> files =  baseMapper.getTimeoutList();
        List<Long> ids = new ArrayList<>(files.size());
        // 删除
        files.forEach(fileRefEntity -> {
            try {
                FileUDUtil.getFile(fileRefEntity.getCode()).delete();
            }catch (Exception e){
                log.error("文件删除失败,{}",ExceptionUtil.buildErrorMessage(e));
            }
        });
        // 批量删除
        baseMapper.deleteBatchIds(ids);
    }

    @Override
    public boolean exists(String code) {
        return count(new QueryWrapper<FileRefEntity>().lambda().eq(FileRefEntity::getCode, code)) == 1;
    }
}
