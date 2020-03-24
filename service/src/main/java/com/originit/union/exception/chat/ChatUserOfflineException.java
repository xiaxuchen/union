package com.originit.union.exception.chat;

import com.originit.common.enums.ResultCode;

/**
 * 用户已离线，无法连接或发送信息
 */
public class ChatUserOfflineException extends ChatException {

    public ChatUserOfflineException() {
        super(ResultCode.CHAT_USER_IS_OFFLINE);
    }

    public ChatUserOfflineException(String message) {
        super(message);
    }
}
