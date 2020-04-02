package com.originit.union.exception.chat;

import com.originit.common.exceptions.enums.ResultCode;

public class ChatUserAlreadyReceiveException extends ChatException {

    public ChatUserAlreadyReceiveException() {
        super();
    }

    public ChatUserAlreadyReceiveException(String message) {
        super(message);
    }

    public ChatUserAlreadyReceiveException(String format, Object... objects) {
        super(format, objects);
    }

    public ChatUserAlreadyReceiveException(ResultCode resultCode, Object data) {
        super(resultCode, data);
    }

    public ChatUserAlreadyReceiveException(ResultCode resultCode) {
        super(resultCode);
    }

    @Override
    public ResultCode defaultResultCode() {
        return ResultCode.CHAT_USER_IS_ALREADY_RECEIVED;
    }
}
