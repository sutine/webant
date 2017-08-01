package org.webant.queen.commons.exception;

public class QueenException extends Exception {
    public QueenException(String message) {
        super(message);
    }

    public QueenException(String message, Throwable cause) {
        super(message, cause);
    }
}
