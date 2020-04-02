package com.originit.union.api.exhandler;

import com.originit.common.exceptions.enums.ResultCode;
import com.originit.common.exceptions.BusinessException;
import com.originit.common.exceptions.RemoteAccessException;
import com.originit.common.util.ExceptionUtil;
import com.originit.union.api.util.ShiroUtils;
import com.originit.union.entity.SysUserEntity;
import com.soecode.wxtools.bean.result.WxError;
import com.soecode.wxtools.exception.WxErrorException;
import com.xxc.common.utils.IpUtil;
import com.xxc.common.utils.RequestContextHolderUtil;
import com.xxc.response.result.PlatformResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 异常处理器，处理所有的异常，记录下日志
 * @author xxc、
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(value = BusinessException.class)
    public PlatformResult businessExceptionHandler(BusinessException exception){
        log.warn("{},Exception {} find, message is {}, code is ",getRequesterInfo(),exception.getClass().getName(),exception.getMessage(),exception.getCode());
        return new PlatformResult<>(false,exception.getCode(), exception.getMessage(), exception.getData());
    }

    @ExceptionHandler(value = RemoteAccessException.class)
    public PlatformResult remoteAccessExceptionHandler (RemoteAccessException exception) {
        log.warn("{},Exception {} find, message is {}, code is ",getRequesterInfo(),exception.getClass().getName(),exception.getMessage(),exception.getCode());
        final WxError wxError = ((WxErrorException) exception.getThrowable()).getError();
        switch (wxError.getErrcode()) {
            case 50005: return new PlatformResult<Object>(false,exception.getCode(), "用户未关注公众号", exception.getData());
            case 45015: return new PlatformResult<Object>(false,exception.getCode(), "回复超时，无法下发消息", exception.getData());
            case 45047: return new PlatformResult<Object>(false,exception.getCode(), "消息发送超过限制(20条)，需等待用户回复", exception.getData());
        }
        return new PlatformResult<Object>(false,exception.getCode(), exception.getMessage(), exception.getData());
    }


    /**
     * 身份验证失败
     * @param e
     */
    @ExceptionHandler(value = AuthenticationException.class)
    public PlatformResult authenticationExceptionHandler (AuthenticationException e) {
        ;
        final Throwable cause = e.getCause();
        if (cause instanceof BusinessException) {
            final BusinessException businessException = (BusinessException) cause;
            log.error("{},the unHandled AuthenticationException {}",getRequesterInfo(), ExceptionUtil.buildErrorMessage(businessException));
            return new PlatformResult<>(false,businessException.getCode(),
                    businessException.getMessage(),null);
        }else {
            log.error("{},the unHandled AuthenticationException {}",getRequesterInfo(), ExceptionUtil.buildErrorMessage(e));
        }
        return new PlatformResult<>(false,ResultCode.USER_LOGIN_ERROR.code(),ResultCode.USER_LOGIN_ERROR.message(),null);
    }

    /**
     * 权限验证失败,TODO 目前没有做权限管理，后期需要添加
     */
    @ExceptionHandler(value = AuthorizationException.class)
    public PlatformResult authorizationExceptionHandler(AuthorizationException exception){
        log.error("{},the unHandled AuthorizationException {}",getRequesterInfo(), ExceptionUtil.buildErrorMessage(exception));
        return new PlatformResult<>(false, ResultCode.PERMISSION_NO_ACCESS.code(), "权限不足", null);
    }



    @ExceptionHandler(value = Exception.class)
    public PlatformResult unHandledExceptionHandler(Exception exception){
        log.error("{},the unHandled Exception {}",getRequesterInfo(), ExceptionUtil.buildErrorMessage(exception));
        ResultCode paramIsInvalid = ResultCode.SYSTEM_INNER_ERROR;
        return new PlatformResult<>(false, paramIsInvalid.code(),paramIsInvalid.message(),null);
    }

    /**
     * 获取请求
     * @return 用户的请求
     */
    private HttpServletRequest getRequest () {
        return RequestContextHolderUtil.getRequest();
    }

    /**
     * 获取请求者的信息
     * @return 请求信息
     */
    private String getRequesterInfo () {
        HttpServletRequest request = getRequest();
        SysUserEntity userInfo = ShiroUtils.getUserInfo();
        StringBuilder builder = new StringBuilder();
        builder.append(IpUtil.getRealIp(request))
                .append("  ")
                .append(request.getMethod())
                .append("  ")
                .append(request.getRequestURI())
                .append("  ");
        if (userInfo != null) {
            builder.append("Requester is ")
                    .append(userInfo.getUserId());
        }
        return builder.toString();
    }
}
