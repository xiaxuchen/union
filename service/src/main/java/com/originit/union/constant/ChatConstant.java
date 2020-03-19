package com.originit.union.constant;

public interface ChatConstant {

    /**
     * 用户等待的列表
     */
    String USER_WAIT_LIST_KEY = "chat-user-wait-list";

    /**
     * 等待用户的id和index的位置对应
     */
    String USER_WAIT_MAP_KEY = "chat-user-wait-map";

    /**
     * 用户的锁
     */
    String USER_LOCK = "chat-user-lock";

    /**
     * 已响应的用户的Map
     */
    String USER_RESPOND_MAP_KEY = "chat-user-respond-map";

    /**
     * 聊天session的前缀
     */
    String SESSION_LIST_KEY_PREFIX = "chat-session-map:";

    /**
     * 当前经理的状态
     */
    String AGENT_MAP_KEY = "chat-agent-map";

}
