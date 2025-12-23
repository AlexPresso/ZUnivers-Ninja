package me.alexpresso.zuninja.exceptions;

import lombok.Getter;

import java.util.Map;

@Getter
public class BaseCustomException extends Exception {
    private final Object errors;

    public BaseCustomException(String message) {
        this(message, Map.of());
    }

    public BaseCustomException(String message, Object errors) {
        super(message);
        this.errors = errors;
    }
}
