package com.originit.union.api.chat.data;

import com.originit.union.entity.UserBindEntity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private String receiveAgent;

    /**
     * 未读列表
     */
    private List<Message> messageList;


//    /**
//     * 添加用户发送的消息
//     * @param openId 用户id
//     * @param message 消息
//     * @param sendTime 发送时间
//     */
//    public void addMessage (String openId, String message, LocalDateTime sendTime) {
//        this.messageList.add(ChatMessage.builder().content(message)
//                .from(openId).isFromUser(true).createTime(sendTime).build());
//    }

}
