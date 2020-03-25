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

    /**
     * 等待用户的数量更新
     */
    String WS_WAIT_COUNT_UPDATE = "/chatUser/waiting/count";

    /**
     * 用户被接受了
     */
    String WS_USER_RECEIVED = "/chatUser/user/received";

    /**
     * 用户结束了聊天
     */
    String WS_EXIT_CHAT = "/chatUser/user/exit";

    /**
     * 用户有新消息
     */
    String WS_NEW_MESSAGE = "/chatUser/message";

    /**
     * 用户的消息数量更新
     */
    String WS_MESSAGE_COUNT_UPDATE = "/chatUser/message/count";
}
