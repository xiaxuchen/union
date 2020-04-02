package com.originit.union.entity.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

@Data
public class SysUserCreateDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户名
     */
    @Length(min = 6,max = 16)
    private String username;

    /**
     * 头像
     */
    private String headImg;
    /**
     * 密码
     */
    private String password;

    /**
     * 是否为客服
     */
    private Boolean isAgent;

    /**
     * 是否有效
     */
    private Boolean isValid;

    /**
     * 客户经理姓名
     */
    private String name;

    private String salt;

    /**
     * 电话号码
     */
    private String phone;

    /**
     * 客服描述
     */
    private String des;

    /**
     * 用户性别
     */
    private Integer sex;

}
