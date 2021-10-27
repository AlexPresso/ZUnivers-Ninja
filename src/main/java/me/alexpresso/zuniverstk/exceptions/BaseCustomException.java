package me.alexpresso.zuniverstk.exceptions;

import java.util.Map;

public class BaseCustomException extends Exception {
    private final Object errors;

    public BaseCustomException(String message) {
        this(message, Map.of());
    }

    public BaseCustomException(String message, Object errors) {
        super(message);
        this.errors = errors;
    }

    public Object getErrors() {
        return this.errors;
    }
}