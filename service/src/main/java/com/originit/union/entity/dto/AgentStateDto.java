package com.originit.union.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 用户更新经理设置的传输对象
 */
@Data
public class AgentStateDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 经理的Id
     */
    @NotNull
    private Long userId;

    /**
     * 自动回复信息
     */
    private String autoResponse;

    /**
     * 是否启用自动回复
     */
    private Boolean autoResponseEnable;

    /**
     * 经理状态
     */
    private Integer state;

    /**
     * 当人数低于该值时自动接入
     */
    private Integer autoReceiveFrom;

    /**
     * 启用自动接收
     */
    private Boolean autoReceiveEnable;
}
