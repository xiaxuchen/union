package com.originit.union.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Description 系统用户实体
 * @Author Sans
 * @CreateTime 2019/6/14 15:57
 */
@Data
@TableName("sys_user")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SysUserEntity implements Serializable {

	public static final int FORBID = 0;

	public static final int ENABLE = 1;

	private static final long serialVersionUID = 1L;
	/**
	 * 用户ID
	 */
	@TableId
	private Long userId;
	/**
	 * 用户名
	 */
	private String username;

	/**
	 * 头像
	 */
	private String headImg;
	/**
	 * 密码
	 */
	private String password;

	/**
	 * 电话号码
	 */
	private String phone;

	/**
	 * 盐值
	 */
	private String salt;
	/**
	 * 状态:NORMAL正常  PROHIBIT禁用
	 */
	private Integer state;

	/**
	 * 是否已删除
	 */
	@TableLogic
	private Integer deleted;

	/**
	 * 创建时间
	 */
	private LocalDateTime gmtCreate;

	/**
	 * 修改时间
	 */
	private LocalDateTime gmtModified;

}
