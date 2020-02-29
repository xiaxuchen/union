package com.originit.union.entity.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysUserVO {
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 用户名
     */
    private String username;

    /**
     * 头像
     */
    private String headImg;

    /**
     * 电话号码
     */
    private String phone;

    /**
     * 状态:NORMAL正常  PROHIBIT禁用
     */
    private Integer state;

    /**
     * 客户经理id
     */
    private Long agentId;

    /**
     * 客户经理姓名
     */
    private String name;

    /**
     * 性别
     */
    private Integer sex;

    /**
     * 客服账号
     */
    private String account;

    /**
     * 简介
     */
    private String des;

    /**
     * 创建时间
     */
    private LocalDateTime gmtCreate;
}
