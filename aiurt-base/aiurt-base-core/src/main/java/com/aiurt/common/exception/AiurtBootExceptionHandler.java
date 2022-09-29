package com.aiurt.common.exception;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.enums.SentinelErrorInfoEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.exception.JeecgBoot401Exception;
import org.jeecg.common.exception.JeecgBootException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.connection.PoolException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceException;
import java.net.ConnectException;

/**
 * 异常处理器
 *
 * @Author aiurt
 * @Date 2022
 */
@RestControllerAdvice
@Slf4j
public class AiurtBootExceptionHandler {

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(AiurtBootException.class)
    public Result<?> handleAiurtBootException(AiurtBootException e) {
        log.error(e.getMessage(), e);
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(Aiurt401Exception.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<?> handleAiurt401Exception(Aiurt401Exception e) {
        log.error(e.getMessage(), e);
        return new Result(401, e.getMessage());
    }

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(JeecgBootException.class)
    public Result<?> handleJeecgBootException(JeecgBootException e) {
        log.error(e.getMessage(), e);
        return Result.error(e.getMessage());
    }

    /**
     * 处理自定义微服务异常
     */
    @ExceptionHandler(JeecgCloudException.class)
    public Result<?> handleJeecgCloudException(JeecgCloudException e) {
        log.error(e.getMessage(), e);
        return Result.error(e.getMessage());
    }

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(JeecgBoot401Exception.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<?> handleJeecgBoot401Exception(JeecgBoot401Exception e) {
        log.error(e.getMessage(), e);
        return new Result(401, e.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public Result<?> handlerNoFoundException(Exception e) {
        log.error(e.getMessage(), e);
        return Result.error(404, "路径不存在，请检查路径是否正确");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public Result<?> handleDuplicateKeyException(DuplicateKeyException e) {
        log.error(e.getMessage(), e);
        return Result.error("数据库中已存在该记录");
    }

    @ExceptionHandler({UnauthorizedException.class, AuthorizationException.class})
    public Result<?> handleAuthorizationException(AuthorizationException e) {
        log.error(e.getMessage(), e);
        return Result.noauth("没有权限，请联系管理员授权");
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error(e.getMessage(), e);
        //update-begin---author:zyf ---date:20220411  for：处理Sentinel限流自定义异常
        Throwable throwable = e.getCause();
        SentinelErrorInfoEnum errorInfoEnum = SentinelErrorInfoEnum.getErrorByException(throwable);
        if (ObjectUtil.isNotEmpty(errorInfoEnum)) {
            return Result.error(errorInfoEnum.getError());
        }
        //update-end---author:zyf ---date:20220411  for：处理Sentinel限流自定义异常
        return Result.error("操作失败，" + e.getMessage());
    }

    /**
     * @param e
     * @return
     * @Author 政辉
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<?> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        StringBuffer sb = new StringBuffer();
        sb.append("不支持");
        sb.append(e.getMethod());
        sb.append("请求方法，");
        sb.append("支持以下");
        String[] methods = e.getSupportedMethods();
        if (methods != null) {
            for (String str : methods) {
                sb.append(str);
                sb.append("、");
            }
        }
        log.error(sb.toString(), e);
        //return Result.error("没有权限，请联系管理员授权");
        return Result.error(405, sb.toString());
    }

    /**
     * spring默认上传大小100MB 超出大小捕获异常MaxUploadSizeExceededException
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result<?> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error(e.getMessage(), e);
        return Result.error("文件大小超出10MB限制, 请压缩或降低文件质量! ");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public Result<?> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error(e.getMessage(), e);
        return Result.error("字段太长,超出数据库字段的长度");
    }

    @ExceptionHandler(PoolException.class)
    public Result<?> handlePoolException(PoolException e) {
        log.error(e.getMessage(), e);
        return Result.error("Redis 连接异常!");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        return Result.error(e.getBindingResult().getFieldError().getDefaultMessage());
    }

    /**
     * 处理自定义异常，没有找到对应的数据
     * InvalidDataFieldException
     *
     * @param e
     * @return
     */
    @ExceptionHandler(AiurtNoDataException.class)
    public Result<?> handleAiurtNoDataException(AiurtNoDataException e) {
        return Result.OK(e.getMessage(), e.getData());
    }


    /**
     * 无效的实体对象字段异常。
     *
     * @param ex      异常对象。
     * @param request http请求。
     * @return 应答对象。
     */
    @ExceptionHandler(value = InvalidDataFieldException.class)
    public Result<Void> invalidDataFieldExceptionHandle(Exception ex, HttpServletRequest request) {
        log.error("InvalidDataFieldException exception from URL [" + request.getRequestURI() + "]", ex);
        return Result.error(AiurtErrorEnum.INVALID_DATA_FIELD.getCode(), AiurtErrorEnum.INVALID_DATA_FIELD.getMessage());
    }

    /**
     * 验证异常处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e) {
        log.error(e.getMessage(), e);
        return Result.error(e.getMessage());
    }

    /**
     * webservice连接异常处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(ConnectException.class)
    public Result<?> handleConnectException(ConnectException e) {
        log.error(e.getMessage(), e);
        return Result.error("连接异常,尝试开启VPN后重试");
    }
    /**
     * webservice连接异常处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(WebServiceException.class)
    public Result<?> handleWebServiceException(WebServiceException e) {
        log.error(e.getMessage(), e);
        return Result.error("远程机器人连接超时");
    }
}
