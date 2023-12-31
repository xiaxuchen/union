package com.originit.common.exceptions;


import com.originit.common.exceptions.enums.ResultCode;

/**
 * @author zhumaer
 * @desc 方法不允许异常
 * @since 5/21/2017 3:00 PM
 */
public class MethodNotAllowException extends BusinessException {

    private static final long serialVersionUID = -3813290937049524713L;

    public MethodNotAllowException() {
        super();
    }

    public MethodNotAllowException(Object data) {
        super.data = data;
    }

    public MethodNotAllowException(ResultCode resultCode) {
        super(resultCode);
    }

    public MethodNotAllowException(ResultCode resultCode, Object data) {
        super(resultCode, data);
    }

    public MethodNotAllowException(String msg) {
        super(msg);
    }

    public MethodNotAllowException(String formatMsg, Object... objects) {
        super(formatMsg, objects);
    }


}
