package com.originit.common.exceptions;

import com.originit.common.exceptions.enums.ResultCode;

/**
 * 用户相关异常
 */
public class UserException extends PermissionForbiddenException {

    public UserException() {
    }

    public UserException(Object data) {
        super(data);
    }

    public UserException(ResultCode resultCode) {
        super(resultCode);
    }

    public UserException(ResultCode resultCode, Object data) {
        super(resultCode, data);
    }

    public UserException(String msg) {
        super(msg);
    }

    public UserException(String formatMsg, Object... objects) {
        super(formatMsg, objects);
    }
}
