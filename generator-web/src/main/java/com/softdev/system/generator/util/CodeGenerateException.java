package com.softdev.system.generator.util;

/**
 * @author xuxueli 2018-05-02 21:10:28
 */
public class CodeGenerateException extends RuntimeException {
    private static final long serialVersionUID = 42L;

    public CodeGenerateException() {
    }

    public CodeGenerateException(String message) {
        super(message);
    }

    public CodeGenerateException(String message, Throwable cause) {
        super(message, cause);
    }

    public CodeGenerateException(Throwable cause) {
        super(cause);
    }

    public CodeGenerateException(String message, Throwable cause, boolean enableSuppression,
                                 boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
