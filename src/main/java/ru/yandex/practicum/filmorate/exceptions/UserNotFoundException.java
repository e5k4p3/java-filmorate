package ru.yandex.practicum.filmorate.exceptions;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(final String message) {
        super(message);
    }
}
