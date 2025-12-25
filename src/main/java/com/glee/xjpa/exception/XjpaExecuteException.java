package com.glee.xjpa.exception;

/**
 * @author GENSEN
 * @date 2022/11/5
 * @description
 */
public class XjpaExecuteException extends XjpaException {

    public XjpaExecuteException(Throwable e) {
        super(e);
    }

    public XjpaExecuteException() {
        super();
    }

    public XjpaExecuteException(String message) {
        super(message);
    }

    public XjpaExecuteException(String message, Throwable cause) {
        super(message, cause);
    }
}
