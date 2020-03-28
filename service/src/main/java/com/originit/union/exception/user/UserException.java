package com.originit.union.exception.user;

import com.originit.common.enums.ResultCode;
import com.originit.common.exceptions.BusinessException;

public class UserException extends BusinessException {

    public UserException() {
    }

    public UserException(String message) {
        super(message);
    }

    @Override
    public ResultCode defaultResultCode() {
        return ResultCode.USER_ERROR;
    }
}
