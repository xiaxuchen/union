package com.originit.union.exception.chat;

import com.originit.common.enums.ResultCode;
import com.originit.common.exceptions.BusinessException;

/**
 * 聊天的异常的基类
 */
public class ChatException extends BusinessException {

    public ChatException() {
        super(ResultCode.CHAT_ERROR);
    }

    public ChatException(String message) {
        super(message);
    }

    public ChatException(String format, Object... objects) {
        super(format, objects);
    }

    public ChatException(ResultCode resultCode, Object data) {
        super(resultCode, data);
    }

    public ChatException(ResultCode resultCode) {
        super(resultCode);
    }
}
