package com.originit.union.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户添加标签的dto
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagUserAddDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String phone;
}
