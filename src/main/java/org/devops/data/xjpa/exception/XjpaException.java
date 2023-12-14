package org.devops.data.xjpa.exception;

import javax.persistence.PersistenceException;

/**
 * @author GENSEN
 * @date 2022/11/5
 * @description model
 */
public class XjpaException extends PersistenceException {

    public XjpaException() {
        super();
    }

    public XjpaException(String message) {
        super(message);
    }

    public XjpaException(String message, Throwable cause) {
        super(message, cause);
    }

    public XjpaException(Throwable cause) {
        super(cause);
    }
}
