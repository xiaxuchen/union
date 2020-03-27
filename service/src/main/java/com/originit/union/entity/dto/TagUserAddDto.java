package com.originit.union.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户添加标签的dto
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagUserAddDto {

    private Long id;

    private String phone;
}
