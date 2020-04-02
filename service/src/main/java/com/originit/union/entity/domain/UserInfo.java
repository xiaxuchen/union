package com.originit.union.entity.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.originit.union.entity.TagEntity;
import com.originit.union.entity.UserBindEntity;
import com.originit.union.entity.vo.TagInfoVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 创建时间
     */
    private LocalDateTime gmtCreate;

    /**
     * 修改时间
     */
    private LocalDateTime gmtModified;

    /**
     * 微信公众号唯一标识
     */
    private String openId;

    /**
     * 绑定的电话号码
     */
    private String phone;

    /**
     * 性别
     */
    private Integer sex;

    /**
     * 昵称
     */
    private String name;

    /**
     * 头像
     */
    private String headImg;

    /**
     * 本月通过平台手推次数
     */
    private Integer pushCount;

    /**
     * 订阅时间
     */
    private LocalDateTime subscribeTime;

    /**
     * 上次使用时间
     */
    private LocalDateTime gmtLastUse;
    /**
     * 用户的标签
     */
    private List<TagInfoVO> tags;
}
