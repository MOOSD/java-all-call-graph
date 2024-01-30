package com.adrninistrator.jacg.exception;

/**
 * runner执行异常
 */
public class RunnerBreakException extends RuntimeException{
    public RunnerBreakException() {
    }

    public RunnerBreakException(String message) {
        super(message);
    }

    public RunnerBreakException(String message, Throwable cause) {
        super(message, cause);
    }

    public RunnerBreakException(Throwable cause) {
        super(cause);
    }

    public RunnerBreakException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
