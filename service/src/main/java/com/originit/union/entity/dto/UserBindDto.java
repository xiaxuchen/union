package com.originit.union.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author super
 * @date 2020/2/6 22:42
 * @description 执念
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserBindDto {
    private String openid;
    private  String phone ;
}
