package ru.yandex.practicum.filmorate.exceptionhandler.exceptions;

public class FilmNotFoundException extends RuntimeException {
    public FilmNotFoundException(final String message) {
        super(message);
    }
}
