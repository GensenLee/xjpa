package com.glee.xjpa.exception;


import jakarta.persistence.PersistenceException;

/**
 * @author GENSEN
 * @date 2022/11/5
 * @description model
 */
public class XJpaException extends PersistenceException {

    public XJpaException() {
        super();
    }

    public XJpaException(String message) {
        super(message);
    }

    public XJpaException(String message, Throwable cause) {
        super(message, cause);
    }

    public XJpaException(Throwable cause) {
        super(cause);
    }
}
