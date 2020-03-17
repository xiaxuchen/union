package com.originit.union.chat.data;

import com.originit.common.util.JsonUtil;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentSkipListMap;

@Getter
@Component
public class ChatInfoMap {

    /**
     * 已接受的客户经理对应的用户列表
     */
    private final ConcurrentSkipListMap<Long, ConcurrentSkipListMap<String,ChatUser>> agentReceiveMap;

    /**
     * 等待人员的map，key为用户的openid，值为用户在等待列表中的索引
     */
    private final ConcurrentSkipListMap<String,Integer> waitMap;

    /**
     * 已接受人员的map，key为用户的openid，值为用户所在用户经理的id
     */
    private final ConcurrentSkipListMap<String,Long> receivedMap;

    /**
     * 存储客户经理的状态
     */
    private final ConcurrentSkipListMap<Long,AgentState> agentMap;


    public ChatInfoMap () {
        agentReceiveMap = new ConcurrentSkipListMap<>();
        waitMap = new ConcurrentSkipListMap<>();
        receivedMap = new ConcurrentSkipListMap<>();
        agentMap = new ConcurrentSkipListMap<>();
    }

    @Override
    public String toString() {
        try {
            return "ChatInfoMap{" +
                    "agentReceiveMap=" + JsonUtil.toJson(agentReceiveMap) +
                    ", waitMap=" + JsonUtil.toJson(waitMap) +
                    ", receivedMap=" + JsonUtil.toJson(receivedMap) +
                    ", agentMap=" + JsonUtil.toJson(agentMap) +
                    '}';
        }catch (Exception e){
            return "";
        }
    }
}
