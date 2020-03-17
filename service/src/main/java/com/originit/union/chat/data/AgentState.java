package com.originit.union.chat.data;

import com.originit.union.entity.AgentInfoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 经理的状态
 * @author xxc、
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AgentState {

    /**
     * 用户头像
     */
    private String headImg;

    /**
     * 客户经理id
     */
    private AgentInfoEntity info;

    /**
     * 经理状态
     */
    private Integer state;

    /**
     * 自动接入的状态
     */
    private Integer autoReceiveState;

    /**
     * 自动接入的数量
     */
    private Integer autoReceiveCount;

    /**
     * 自动接入从当前接入用户有几个开始
     */
    private Integer autoReceiveFrom;
}
