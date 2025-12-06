package com.softdev.system.generator.util.exception;

/**
 * SQL解析异常
 *
 * @author zhengkai.blog.csdn.net
 */
public class SqlParseException extends CodeGenException {

    public SqlParseException() {
    }

    public SqlParseException(String message) {
        super(message);
    }

    public SqlParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public SqlParseException(Throwable cause) {
        super(cause);
    }

    public SqlParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}