package com.originit.union.api.exhandler;

import com.originit.common.enums.ResultCode;
import com.originit.common.exceptions.BusinessException;
import com.originit.common.exceptions.RemoteAccessException;
import com.originit.common.util.ExceptionUtil;
import com.originit.union.api.util.ShiroUtils;
import com.originit.union.entity.SysUserEntity;
import com.xxc.common.utils.IpUtil;
import com.xxc.common.utils.RequestContextHolderUtil;
import com.xxc.response.result.PlatformResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 异常处理器，处理所有的异常，记录下日志
 * @author xxc、
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ResponseBody
    @ExceptionHandler(value = BusinessException.class)
    public PlatformResult businessExceptionHandler(BusinessException exception){
        log.warn("{},Exception {} find, message is {}, code is ",getRequesterInfo(),exception.getClass().getName(),exception.getMessage(),exception.getCode());
        return new PlatformResult<>(false,exception.getCode(), exception.getMessage(), exception.getData());
    }

    @ExceptionHandler(value = RemoteAccessException.class)
    public PlatformResult remoteAccessExceptionHandler (RemoteAccessException exception) {
        return new PlatformResult<Object>(false,exception.getCode(), exception.getMessage(), exception.getData());
    }


    @ResponseBody
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
