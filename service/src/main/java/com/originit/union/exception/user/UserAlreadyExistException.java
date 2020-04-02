package com.originit.union.exception.user;

import com.originit.common.exceptions.enums.ResultCode;

/**
 * 用户已存在异常
 * @author xxc、
 */
public class UserAlreadyExistException extends UserException{

    public UserAlreadyExistException() {
    }

    public UserAlreadyExistException(String message) {
        super(message);
    }

    @Override
    public ResultCode defaultResultCode() {
        return ResultCode.USER_HAS_EXISTED;
    }
}
