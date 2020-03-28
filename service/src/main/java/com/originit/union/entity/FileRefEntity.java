package com.originit.union.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 图片引用实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("file_ref")
public class FileRefEntity {
    /**
     * 图片的id
      */
    @TableId
    private Long id;
    /**
     * 图片的编码
      */
    private String code;

    /**
     * 图片的引用次数
      */
    private Integer count;

    /**
     * 文件超时时间
     */
    private Long expire;

    /**
     * 创建时间
     */
    private LocalDateTime gmtCreate;

    /**
     * 修改时间
     */
    private LocalDateTime gmtModified;
}
