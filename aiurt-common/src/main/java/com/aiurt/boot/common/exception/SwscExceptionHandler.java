package com.aiurt.boot.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.connection.PoolException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 异常处理器
 *
 * @Author swsc
 * @Date 2019
 */
@Component
@RestControllerAdvice
@Slf4j
public class SwscExceptionHandler {

	/**
	 * 处理自定义异常
	 */
	@ExceptionHandler(SwscException.class)
	public Result<?> handleRRException(SwscException e){
		log.error(e.getMessage(), e);
		return Result.error(e.getMessage());
	}

	/**
	 * 参数异常
	 *
	 * @param e
	 * @return
	 */
	@ExceptionHandler({
			ConstraintViolationException.class,
			MethodArgumentNotValidException.class,
			ServletRequestBindingException.class,
			BindException.class})
	public Result<?> handleValidationException(Exception e) {
		String msg;
		if (e instanceof MethodArgumentNotValidException) {
			MethodArgumentNotValidException t = (MethodArgumentNotValidException) e;
			msg = t.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(","));
		} else if (e instanceof BindException) {
			BindException t = (BindException) e;
			msg = t.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(","));
		} else if (e instanceof ConstraintViolationException) {
			ConstraintViolationException t = (ConstraintViolationException) e;
			msg = t.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(","));
		} else if (e instanceof MissingServletRequestParameterException) {
			MissingServletRequestParameterException t = (MissingServletRequestParameterException) e;
			msg = t.getParameterName() + " 不能为空";
		} else if (e instanceof MissingPathVariableException) {
			MissingPathVariableException t = (MissingPathVariableException) e;
			msg = t.getVariableName() + " 不能为空";
		} else {
			msg = "必填参数缺失";
		}
		log.warn("参数校验不通过,msg: {}", msg);
		return Result.error(msg);
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	public Result<?> handlerNoFoundException(Exception e) {
		log.error(e.getMessage(), e);
		return Result.error(404, "路径不存在，请检查路径是否正确");
	}

	@ExceptionHandler(DuplicateKeyException.class)
	public Result<?> handleDuplicateKeyException(DuplicateKeyException e){
		log.error(e.getMessage(), e);
		return Result.error("数据库中已存在该记录");
	}

	@ExceptionHandler({UnauthorizedException.class, AuthorizationException.class})
	public Result<?> handleAuthorizationException(AuthorizationException e){
		log.error(e.getMessage(), e);
		return Result.noauth("没有权限，请联系管理员授权");
	}

	@ExceptionHandler(Exception.class)
	public Result<?> handleException(Exception e){
		log.error(e.getMessage(), e);
		return Result.error("系统发生异常,操作失败.");
	}



	/**
	 * @Author 政辉
	 * @param e
	 * @return
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public Result<?> HttpRequestMethodNotSupportedException(Exception e){
		log.error(e.getMessage(), e);
		return Result.error("没有权限，请联系管理员授权");
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

}
