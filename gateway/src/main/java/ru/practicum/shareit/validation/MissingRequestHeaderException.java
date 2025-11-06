package ru.practicum.shareit.validation;

public class MissingRequestHeaderException extends RuntimeException {
    public MissingRequestHeaderException(String message) {
        super(message);
    }
}
