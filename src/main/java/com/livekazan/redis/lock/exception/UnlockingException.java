package com.livekazan.redis.lock.exception;

/**
 * Exception related to release lock operation.
 */
public class UnlockingException extends Exception {
    private static final long serialVersionUID = -6307757481603355424L;

    public UnlockingException() {
    }

    public UnlockingException(String message) {
        super(message);
    }

    public UnlockingException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnlockingException(Throwable cause) {
        super(cause);
    }

    public UnlockingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
