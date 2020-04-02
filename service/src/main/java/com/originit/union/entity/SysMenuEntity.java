package com.originit.union.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Description 权限实体
 * @Author Sans
 * @CreateTime 2019/6/14 15:57
 */
@Data
@TableName("sys_menu")
public class SysMenuEntity implements Serializable {


	public interface STATE {
		/**
		 * 禁用
		 */
		Integer FORBIDDEN = 0;
		/**
		 * 有效
		 */
		Integer VALID = 1;
	}

	public interface TYPE {
		/**
		 * 页面
		 */
		Integer PAGE = 0;
		/**
		 * 菜单
		 */
		Integer MENU = 1;
		/**
		 * 目录
		 */
		Integer FOLDER = 2;
		/**
		 * 接口权限
		 */
		Integer INTERFACE = 3;
	}

	private static final long serialVersionUID = 1L;

	/**
	 * 权限ID
	 */
	@TableId
	private Long menuId;
	/**
	 * 权限名称
	 */
	private String name;
	/**
	 * 权限标识
	 */
	private String perms;

	/**
	 * 描述信息
	 */
	private String desc;

	/**
	 * 对应的组件
	 */
	private String component;

	/**
	 * 菜单栏的icon
	 */
	private String icon;

	/**
	 * 跳转路径
	 */
	private String path;

	/**
	 * 路由重定向
	 */
	private String redirect;

	/**
	 * 类型 {@link TYPE}
	 */
	private Integer type;

	/**
	 * 状态 {@link STATE}
	 */
	private Integer state;

	/**
	 * 菜单的排序
	 */
	private Integer sort;
	/**
	 * 创建时间
	 */
	private LocalDateTime gmtCreate;

	/**
	 * 修改时间
	 */
	private LocalDateTime gmtModified;
}
