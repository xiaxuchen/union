package com.originit.union.entity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class SysUserQueryDto implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 用户姓名
     */
    private String name;
    /**
     * 用户名
     */
    private String userName;

    /**
     * 电话号码
     */
    private String mobile;
    /**
     * 性别
     */
    private Integer sex;
    /**
     *  角色id
      */
    private Long roleId;
    /**
     * 用户id
     */
    private String userId;

    /**
     * 当前页
     */
    private Integer curPage;

    /**
     * 每页的大小
     */
    private Integer pageSize;
}
