package com.originit.union.exception.chat;

import com.originit.common.exceptions.enums.ResultCode;

/**
 * 用户不是经理
 */
public class UserIsNotAgentException extends ChatException {

    public UserIsNotAgentException() {
        super();
    }

    public UserIsNotAgentException(String message) {
        super(message);
    }

    public UserIsNotAgentException(String format, Object... objects) {
        super(format, objects);
    }

    public UserIsNotAgentException(ResultCode resultCode, Object data) {
        super(resultCode, data);
    }

    public UserIsNotAgentException(ResultCode resultCode) {
        super(resultCode);
    }

    @Override
    public ResultCode defaultResultCode() {
        return ResultCode.USER_IS_NOT_AGENT;
    }
}
