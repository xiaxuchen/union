package com.originit.union.exception.chat;

import com.originit.common.exceptions.enums.ResultCode;
import com.originit.common.exceptions.BusinessException;

/**
 * 聊天的异常的基类
 */
public class ChatException extends BusinessException {

    public ChatException() {
        super();
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

    @Override
    public ResultCode defaultResultCode() {
        return ResultCode.CHAT_ERROR;
    }
}
