package com.glee.xjpa.exception;

import java.io.Serial;

public class XJpaEmptyWhereException extends XJpaException {

	/**
	 * 
	 */
	@Serial
	private static final long serialVersionUID = 1L;
	
	public XJpaEmptyWhereException(String message){
		super(message);
	}

}
