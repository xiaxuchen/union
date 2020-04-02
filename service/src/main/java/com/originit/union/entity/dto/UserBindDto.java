package com.originit.union.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author super
 * @date 2020/2/6 21:19
 * @description 执念
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserBindDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private  String  openid;
    private String phone;

}
