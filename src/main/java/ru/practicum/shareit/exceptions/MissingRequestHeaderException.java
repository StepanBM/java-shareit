package ru.practicum.shareit.exceptions;

public class MissingRequestHeaderException extends RuntimeException {
    public MissingRequestHeaderException(String message) {
        super(message);
    }
}
