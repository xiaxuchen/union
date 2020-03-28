package com.originit.common.exceptions;

import com.originit.common.enums.BusinessExceptionEnum;
import com.originit.common.enums.ResultCode;
import com.originit.common.util.StringUtil;
import lombok.Data;

/**
 * @author zhumaer
 * @desc 业务异常类
 * @since 9/18/2017 3:00 PM
 */
@Data
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 194906846739586856L;

    protected Integer code;

    protected String message;

    protected ResultCode resultCode;

    protected Object data;

    protected Throwable throwable;

    public BusinessException() {
        final ResultCode resultCode = defaultResultCode();
        if (resultCode != null) {
            this.resultCode = resultCode;
            code = resultCode.code();
            message = resultCode.message();
        }
    }

    public BusinessException(String message) {
        this();
        this.message = message;
    }

    public BusinessException(Throwable cause) {
        this();
        this.throwable = cause;
    }

    public BusinessException(Throwable cause,String message) {
        this(message);
        this.throwable = cause;
    }

    public BusinessException(String format, Object... objects) {
        this();
        this.message = StringUtil.formatIfArgs(format, "{}", objects);
    }

    public BusinessException(ResultCode resultCode, Object data) {
        this(resultCode);
        this.data = data;
    }

    public BusinessException(ResultCode resultCode) {
        this.resultCode = resultCode;
        this.code = resultCode.code();
        this.message = resultCode.message();
    }

    /**
     * 获取默认的resultCode
     * @return
     */
    public ResultCode defaultResultCode () {
        BusinessExceptionEnum exceptionEnum = BusinessExceptionEnum.getByEClass(this.getClass());
        if (exceptionEnum != null) {
            return exceptionEnum.getResultCode();
        }
        return null;
    }
}
