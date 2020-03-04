package com.originit.union.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xxc、
 * 角色vo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleVO {
    /**
     * 角色id
      */
    private Long id;
    /**
     * 角色名
     */
    private String name;
}