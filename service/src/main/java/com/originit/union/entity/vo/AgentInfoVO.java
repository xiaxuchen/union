package com.originit.union.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author xxc、
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 客户经理id
     */
    private Long id;

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

}