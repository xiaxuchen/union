package com.originit.union.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author xxc、
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Long id;

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
     * 是否是客户经理
     */
    private Boolean isAgent;

    /**
     * 客户经理名称
     */
    private String name;

    /**
     * 性别
     */
    private Integer sex;

    /**
     * 描述
     */
    private String des;
}
