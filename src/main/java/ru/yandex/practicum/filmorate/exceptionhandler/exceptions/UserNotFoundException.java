package ru.yandex.practicum.filmorate.exceptionhandler.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(final String message) {
        super(message);
    }
}
