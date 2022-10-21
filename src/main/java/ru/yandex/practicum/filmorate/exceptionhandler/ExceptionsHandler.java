package ru.yandex.practicum.filmorate.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.exceptionhandler.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptionhandler.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptionhandler.exceptions.ValidationException;

@RestControllerAdvice(assignableTypes = {FilmController.class, UserController.class})
public class ExceptionsHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(final ValidationException e) {
        return new ErrorResponse("Ошибка валидации.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleFilmNotFound(final FilmNotFoundException e) {
        return new ErrorResponse("Фильм не найден.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound(final UserNotFoundException e) {
        return new ErrorResponse("Пользователь не найден.", e.getMessage());
    }
}
