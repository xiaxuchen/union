package com.originit.union.entity.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户信息vo
 * @author xxc、
 */
@Data
public class SysUserVO {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 客户经理信息
     */
    private AgentInfoVO agentInfo;

    /**
     * 用户的角色
     */
    private List<RoleVO> roles;
    /**
     * 用户名
     */
    private String username;

    /**
     * 头像
     */
    private String headImg;

    /**
     * 电话号码
     */
    private String phone;

    /**
     * 状态:NORMAL正常  PROHIBIT禁用
     */
    private Integer state;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
