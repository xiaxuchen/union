package com.originit.common.exceptions;

import com.originit.common.exceptions.enums.ResultCode;

/**
 * redis分布式加锁失败时产生
 * @author xxc、
 */
public class LockFailException extends BusinessException {

    public LockFailException() {
        super(ResultCode.SYSTEM_LOCK_FAIL);
    }

    public LockFailException(String message) {
        super(ResultCode.SYSTEM_LOCK_FAIL,message);
    }

}
