package com.originit.union.exception.user;

import com.originit.common.exceptions.enums.ResultCode;

/**
 * 用户不存在异常
 * @author xxc、
 */
public class UserNotExistException extends UserException{

    public UserNotExistException() {
    }

    public UserNotExistException(String message) {
        super(message);
    }

    @Override
    public ResultCode defaultResultCode() {
        return ResultCode.USER_NOT_EXIST;
    }
}
