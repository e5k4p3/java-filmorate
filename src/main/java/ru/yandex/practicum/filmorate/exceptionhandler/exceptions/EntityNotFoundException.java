package ru.yandex.practicum.filmorate.exceptionhandler.exceptions;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(final String message) {
        super(message);
    }
}
