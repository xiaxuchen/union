package com.originit.common.exceptions;


import com.originit.common.exceptions.enums.ResultCode;

/**
 * @author zhumaer
 * @desc 数据没有找到异常
 * @since 9/18/2017 3:00 PM
 */
public class DataNotFoundException extends BusinessException {

    private static final long serialVersionUID = 3721036867889297081L;

    public DataNotFoundException() {
        super();
    }

    public DataNotFoundException(Object data) {
        super();
        super.data = data;
    }

    public DataNotFoundException(ResultCode resultCode) {
        super(resultCode);
    }

    public DataNotFoundException(ResultCode resultCode, Object data) {
        super(resultCode, data);
    }

    public DataNotFoundException(String msg) {
        super(msg);
    }

    public DataNotFoundException(String formatMsg, Object... objects) {
        super(formatMsg, objects);
    }

}
