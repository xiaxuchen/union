package com.originit.union.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * @Description 用户与角色关系实体
 * @Author Sans
 * @CreateTime 2019/6/14 15:57
 */
@Data
@TableName("sys_user_role")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SysUserRoleEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * ID
	 */
	@TableId
	private Long id;
	/**
	 * 用户ID
	 */
	private Long userId;
	/**
	 * 角色ID
	 */
	private Long roleId;

	/**
	 * 创建时间
 	 */
	private LocalDateTime gmtCreate;

	/**
	 * 修改时间
 	 */
	private LocalDateTime gmtModified;
}
