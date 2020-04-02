package com.originit.union.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author xxc、
 * 角色vo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleVO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 角色id
      */
    private Long id;
    /**
     * 角色名
     */
    private String name;
}