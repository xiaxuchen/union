package com.originit.union.chat.data;

import com.originit.union.entity.MessageEntity;
import com.originit.union.entity.UserBindEntity;
import lombok.Data;

import java.util.List;

/**
 * 表示聊天是的用户信息，有附加的聊天状态、对话的会话经理、聊天消息列表等信息
 */
@Data
public class ChatUser {

    public interface STATE {
        /**
         *  当前用户没有使用客服功能
         */
        int NEVER = -1;
        /**
         * 当前用户在等待接入
         */
        int WAIT = 0;
        /**
         * 当前用户已经被客户经理接收了
         */
        int RECEIVED = 1;
    }

    /**
     * 待接收的用户信息
     */
    private UserBindEntity userInfo;

    /**
     * 接收的客户经理
     */
    private Long receiveAgent;

    /**
     * 消息列表
     */
    private List<MessageEntity> messageList;


}
