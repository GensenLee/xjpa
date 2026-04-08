package com.glee.xjpa.exception;

public class XJpaInitException extends RuntimeException {

	public XJpaInitException() {
		super();
	}

	public XJpaInitException(String message) {
		super(message);
	}

	public XJpaInitException(String message, Throwable cause) {
		super(message, cause);
	}

	public XJpaInitException(Throwable cause) {
		super(cause);
	}
}
