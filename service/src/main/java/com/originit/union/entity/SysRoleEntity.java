package com.originit.union.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * @Description 角色实体
 * @Author Sans
 * @CreateTime 2019/6/14 15:57
 */
@Data
@TableName("sys_role")
public class SysRoleEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 角色ID
	 */
	@TableId
	private Long roleId;
	/**
	 * 角色名称
	 */
	private String roleName;

	/**
	 * 是否为系统定义，系统不可删除
	 */
	private Boolean isSys;

	/**
	 * 创建时间
	 */
	private LocalDateTime gmtCreate;

	/**
	 * 修改时间
	 */
	private LocalDateTime gmtModified;
}
