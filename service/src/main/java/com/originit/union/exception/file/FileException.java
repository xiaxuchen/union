package com.originit.union.exception.file;

import com.originit.common.exceptions.enums.ResultCode;
import com.originit.common.exceptions.BusinessException;

/**
 * 文件异常的基类
 */
public class FileException extends BusinessException {

    public FileException(Throwable cause,String message) {
        super(cause,message);
    }

    public FileException() {
        super();
    }

    public FileException(String message) {
        super(message);
    }

    @Override
    public ResultCode defaultResultCode() {
        return ResultCode.FILE_ERROR;
    }
}
