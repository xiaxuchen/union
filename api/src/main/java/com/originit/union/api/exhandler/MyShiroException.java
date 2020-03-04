package com.originit.union.api.exhandler;

import com.originit.common.enums.ResultCode;
import com.xxc.response.result.PlatformResult;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
@RestController
public class MyShiroException {
    /**
     * 权限验证失败
     */
    @ExceptionHandler(value = AuthorizationException.class)
    public PlatformResult defaultErrorHandler(){
        return new PlatformResult<>(false, ResultCode.PERMISSION_NO_ACCESS.code(), "权限不足", null);
    }

    /**
     * 身份验证失败
     * @param e
     */
    @ExceptionHandler(value = AuthenticationException.class)
    public PlatformResult authenticationExceptionHandler (AuthenticationException e) {
        return new PlatformResult<>(false,ResultCode.USER_LOGIN_ERROR.code(),ResultCode.USER_LOGIN_ERROR.message(),null);
    }
}
