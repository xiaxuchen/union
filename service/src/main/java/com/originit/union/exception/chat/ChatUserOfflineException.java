package com.originit.union.exception.chat;

import com.originit.common.enums.ResultCode;

/**
 * 用户已离线，无法连接或发送信息
 */
public class ChatUserOfflineException extends ChatException {

    public ChatUserOfflineException() {
        super();
    }

    public ChatUserOfflineException(String message) {
        super(message);
    }

    @Override
    public ResultCode defaultResultCode() {
        return ResultCode.CHAT_USER_IS_OFFLINE;
    }
}
