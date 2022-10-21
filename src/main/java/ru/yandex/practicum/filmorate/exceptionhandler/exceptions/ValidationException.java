package ru.yandex.practicum.filmorate.exceptionhandler.exceptions;

public class ValidationException extends RuntimeException {
    public ValidationException(final String message) {
        super(message);
    }
}
