package com.originit.union.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

@Data
@TableName("user_bind")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBindEntity {
    @TableId
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
     * 性别
     */
    private Integer sex;

    /**
     * 昵称
     */
    private String name;

    /**
     * 头像
     */
    private String headImg;

    /**
     * 本月通过平台手推次数
     */
    private Integer pushCount;

    /**
     * 订阅时间
     */
    private LocalDateTime subscribeTime;

    /**
     * 上次使用时间
     */
    private LocalDateTime gmtLastUse;
}
