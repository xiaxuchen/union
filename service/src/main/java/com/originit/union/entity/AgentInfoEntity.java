package com.originit.union.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Primary;

import java.time.LocalDateTime;

@Data
@TableName("agent_info")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AgentInfoEntity {
    @TableId
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

    /**
     * 修改时间
     */
    private LocalDateTime gmtModified;
}
