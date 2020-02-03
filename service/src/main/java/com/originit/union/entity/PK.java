package com.originit.union.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通用的实体字段
 */
@Data
class PK {
    /**
     * 推送的id
     */
    private Long id;

    /**
     * 创建时间
     */
    private LocalDateTime gmtCreate;

    /**
     * 修改时间
     */
    private LocalDateTime gmtModified;
}
