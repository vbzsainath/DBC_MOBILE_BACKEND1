package com.vbz.dbcards.exception;

/**
 * Thrown when a caller attempts an action they are not permitted to perform.
 * Mapped to HTTP 403 Forbidden by GlobalExceptionHandler.
 *
 * Example: senderId in the request does not match the share record owner.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
