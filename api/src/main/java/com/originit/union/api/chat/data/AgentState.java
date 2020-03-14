package com.originit.union.api.chat.data;

public class AgentState {

    /**
     * 客户经理id
     */
    private Long id;

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
