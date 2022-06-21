package com.aiurt.common.exception;

/**
 * @Description: aiurt自定义401异常
 * @author: aiurt
 */
public class Aiurt401Exception extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public Aiurt401Exception(String message){
		super(message);
	}

	public Aiurt401Exception(Throwable cause)
	{
		super(cause);
	}

	public Aiurt401Exception(String message, Throwable cause)
	{
		super(message,cause);
	}
}
