package com.iprody.xpayment.adapter.app.exception;

public class NonRetrayableException extends RuntimeException {
    public NonRetrayableException(String message) {
        super(message);
    }

    public NonRetrayableException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonRetrayableException(Throwable cause) {
        super(cause);
    }
}
