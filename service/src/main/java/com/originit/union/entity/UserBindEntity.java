package com.originit.union.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

@Data
@TableName("user_bind")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBindEntity {
    private Long id;

    /**
     * 创建时间
     */
    private LocalDateTime gmtCreate;

    /**
     * 修改时间
     */
    private LocalDateTime gmtModified;

    /**
     * 微信公众号唯一标识
     */
    private String openId;

    /**
     * 绑定的电话号码
     */
    private String phone;

    /**
     * 客户经理的id
     */
    private Long agentId;

    /**
     * 上次使用时间
     */
    private LocalDateTime gmtLastUse;
}
