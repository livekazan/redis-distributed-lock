package com.livekazan.redis.lock.exception;

/**
 * Exception related to acquiring lock operation.
 */
public class LockingException extends Exception {

    private static final long serialVersionUID = -7922327854970283981L;

    public LockingException() {
    }

    public LockingException(String message) {
        super(message);
    }

    public LockingException(String message, Throwable cause) {
        super(message, cause);
    }

    public LockingException(Throwable cause) {
        super(cause);
    }

    public LockingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
