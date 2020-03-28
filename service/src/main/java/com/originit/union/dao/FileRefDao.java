package com.originit.union.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.originit.union.entity.FileRefEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author xxc、
 */
@Repository
public interface FileRefDao extends BaseMapper<FileRefEntity> {
    /**
     * 获取超时的文件信息
     * @return 超时文件信息列表
     */
    List<FileRefEntity> getTimeoutList();

    /**
     * 自增引用数count
     * @param code 图片的code
     * @return 受影响数
     */
    int increCount(@Param("code") String code);

    /**
     * 引用数量自减
     * @param code 文件编码
     * @return 受影响数
     */
    int decreCount(@Param("code") String code);
}
