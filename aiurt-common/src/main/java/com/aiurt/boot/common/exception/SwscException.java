package com.aiurt.boot.common.exception;

public class SwscException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public SwscException(String message){
		super(message);
	}

	public SwscException(Throwable cause)
	{
		super(cause);
	}

	public SwscException(String message, Throwable cause)
	{
		super(message,cause);
	}
}
