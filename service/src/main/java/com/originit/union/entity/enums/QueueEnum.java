package com.originit.union.entity.enums;

import lombok.Getter;

/**
 * 消息队列的枚举
 * @author xxc、
 */

@Getter
public enum QueueEnum {

    /**
     * 聊天队列
     */
    QUEUE_MESSAGE_HANDLE("union.message.direct", "union.message.receive", "union.message.receive");

    /**
     * 交换名称
     */
    private String exchange;
    /**
     * 队列名称
     */
    private String name;
    /**
     * 路由键
     */
    private String routeKey;

    QueueEnum(String exchange, String name, String routeKey) {
        this.exchange = exchange;
        this.name = name;
        this.routeKey = routeKey;
    }
}
