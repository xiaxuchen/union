package com.originit.union.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author xxc、
 */
@Data
@AllArgsConstructor
public class LoginUserVO {

    /**
     * 用户名
     */
    private String username;

    /**
     * 头像
     */
    private String headImg;
}
