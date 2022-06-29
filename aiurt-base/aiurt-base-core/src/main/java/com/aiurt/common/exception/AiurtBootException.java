package com.aiurt.common.exception;

import com.aiurt.common.constant.CommonConstant;

/**
 * @Description: aiurt自定义异常
 * @author: aiurt
 */
public class AiurtBootException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private Integer errCode = CommonConstant.SC_INTERNAL_SERVER_ERROR_500;

	public AiurtBootException(String message){
		super(message);
	}

	public AiurtBootException(Integer errCode, String message){
		super(message);
		this.errCode = errCode;
	}

	public AiurtBootException(Throwable cause) {
		super(cause);
	}

	public AiurtBootException(String message, Throwable cause) {
		super(message,cause);
	}

	public AiurtBootException(Integer errCode, String message, Throwable cause) {
		super(message,cause);
		this.errCode = errCode;
	}

	public Integer getCode()
	{
		return errCode;
	}

	public void setCode(int code)
	{
		this.errCode = code;
	}
}
