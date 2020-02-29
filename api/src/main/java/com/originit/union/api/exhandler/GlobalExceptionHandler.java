package com.originit.union.api.exhandler;

import com.originit.common.exceptions.BusinessException;
import com.xxc.response.result.PlatformResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ResponseBody
    @ExceptionHandler(value = BusinessException.class)
    public PlatformResult businessExceptionHandler(BusinessException exception){
        exception.printStackTrace();
        log.error(exception.getMessage());
        return new PlatformResult<>(false,exception.getCode(), exception.getMessage(), exception.getData());
    }

//    @ResponseBody
//    @ExceptionHandler(value = IllegalArgumentException.class)
//    public PlatformResult businessExceptionHandler(IllegalArgumentException exception){
//        log.error(exception.getMessage());
//        ResultCode paramIsInvalid = ResultCode.PARAM_IS_INVALID;
//        return new PlatformResult<>(false, paramIsInvalid.code(),paramIsInvalid.message(),null);
//    }
//
//    @ResponseBody
//    @ExceptionHandler(value = Exception.class)
//    public PlatformResult businessExceptionHandler(Exception exception){
//        log.error(exception.getMessage());
//        ResultCode paramIsInvalid = ResultCode.SYSTEM_INNER_ERROR;
//        return new PlatformResult<>(false, paramIsInvalid.code(),paramIsInvalid.message(),null);
//    }
}
