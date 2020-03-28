package com.originit.union.exception.file;

import com.originit.common.enums.ResultCode;

/**
 * 文件找不到异常
 */
public class FileNotFoundException extends FileException {

    public FileNotFoundException() {
        super();
    }

    public FileNotFoundException(String message) {
        super(message);
    }

    public FileNotFoundException(Throwable cause, String message) {
        super(cause, message);
    }

    @Override
    public ResultCode defaultResultCode() {
        return ResultCode.FILE_NOT_FOUND_ERROR;
    }
}
