package com.softdev.system.generator.util;

import java.io.Serial;

/**
 * @author xuxueli 2018-05-02 21:10:28
 */
public class SqlException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 42L;

    public SqlException() {
        super();
    }

    public SqlException(String msg) {
        super(msg);
    }

    public SqlException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SqlException(Throwable cause) {
        super(cause);
    }

    public SqlException(String message, Throwable cause,
                        boolean enableSuppression,
                        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
