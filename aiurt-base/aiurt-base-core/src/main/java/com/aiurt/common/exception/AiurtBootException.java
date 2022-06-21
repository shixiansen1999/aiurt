package com.aiurt.common.exception;

/**
 * @Description: aiurt自定义异常
 * @author: aiurt
 */
public class AiurtBootException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public AiurtBootException(String message){
		super(message);
	}

	public AiurtBootException(Throwable cause)
	{
		super(cause);
	}

	public AiurtBootException(String message, Throwable cause)
	{
		super(message,cause);
	}
}
