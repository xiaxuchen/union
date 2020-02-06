package com.originit.union.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_bind")
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
     * 上次使用时间
     */
    private LocalDateTime gmtLastUse;
}
