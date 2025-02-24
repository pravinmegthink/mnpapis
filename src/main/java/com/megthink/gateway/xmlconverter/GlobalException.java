package com.megthink.gateway.xmlconverter;

public class GlobalException extends RuntimeException {

    public GlobalException(String message) {
        super(message);
    }

    public GlobalException(Throwable ex) {
        super(ex);
    }

    public GlobalException(String message, Throwable cause) {
        super(message, cause);
    }

    public GlobalException() {
        super();
    }
}
