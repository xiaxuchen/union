package com.originit.union.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@TableName("user_bind")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBindEntity extends PK {

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
