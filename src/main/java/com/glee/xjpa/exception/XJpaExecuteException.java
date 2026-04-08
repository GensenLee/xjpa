package com.glee.xjpa.exception;

/**
 * @author GENSEN
 * @date 2022/11/5
 * @description
 */
public class XJpaExecuteException extends XJpaException {

    public XJpaExecuteException(Throwable e) {
        super(e);
    }

    public XJpaExecuteException() {
        super();
    }

    public XJpaExecuteException(String message) {
        super(message);
    }

    public XJpaExecuteException(String message, Throwable cause) {
        super(message, cause);
    }
}
