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
 * 存储经理聊天状态对象
 */
@TableName("agent_state")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentStateEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public interface STATE {
        /**
         * 离线
         */
        int OFF_WORK = 0;
        /**
         * 在线
         */
        int ON_LINE = 1;
    }

    @TableId
    private Long agentStateId;

    /**
     * 经理的Id
     */
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
     * 从第几个开始接收
     */
    private Integer autoReceiveFrom;

    /**
     * 最大的接受人数
     */
    private Integer autoReceiveMax;

    /**
     * 启用自动接收
     */
    private Boolean autoReceiveEnable;

    /**
     * 创建时间
     */
    private LocalDateTime gmtCreate;

    /**
     * 修改时间
     */
    private LocalDateTime gmtModified;
}

