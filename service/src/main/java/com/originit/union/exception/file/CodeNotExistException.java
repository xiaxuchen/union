package com.originit.union.exception.file;

import com.originit.common.exceptions.enums.ResultCode;

public class CodeNotExistException extends FileException {

    public CodeNotExistException() {
    }

    public CodeNotExistException(String message) {
        super(message);
    }

    @Override
    public ResultCode defaultResultCode() {
        return ResultCode.FILE_CODE_NOT_EXIST;
    }
}
