package com.originit.union.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("agent_info")
public class AgentInfoEntity {

    private Long id;

    /**
     * 关联的用户id
     */
    private Long sysUserId;

    /**
     * 客户经理姓名
     */
    private String name;

    /**
     * 形变
     */
    private String sex;

    /**
     * 客服账号
     */
    private String account;

    /**
     * 简介
     */
    private String desc;

    /**
     * 创建时间
     */
    private LocalDateTime gmtCreate;

    /**
     * 修改时间
     */
    private LocalDateTime gmtModified;
}
