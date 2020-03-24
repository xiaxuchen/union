package com.originit.union.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * websocket的消息实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebSocketMessage<T> implements Serializable {

    public interface TYPE {
        int MESSAGE = 0;

        int EVENT = 1;
    }

    /**
     * 消息类型，0普通消息，1事件推送
     */
    public Integer type;

    /**
     * 消息内容
     */
    public T content;

    /**
     * 事件名称
     */
    public String eventName;
}
