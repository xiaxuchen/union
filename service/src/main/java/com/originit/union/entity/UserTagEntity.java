package com.originit.union.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_tag")
public class UserTagEntity {

    @TableId
    private Long id;

    /**
     * 微信用户id
     */
    private Long bindUserId;

    /**
     * 标签的id
     */
    private Long tagId;

    /**
     * 创建时间
     */
    private LocalDateTime gmtCreate;

    /**
     * 修改时间
     */
    private LocalDateTime gmtModified;
}
