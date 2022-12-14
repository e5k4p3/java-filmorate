package ru.yandex.practicum.filmorate.exceptionhandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.controllers.GenreController;
import ru.yandex.practicum.filmorate.controllers.MpaController;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.exceptionhandler.exceptions.EntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptionhandler.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exceptionhandler.exceptions.ValidationException;

@Slf4j
@RestControllerAdvice(assignableTypes = {FilmController.class,
                                         UserController.class,
                                         MpaController.class,
                                         GenreController.class})
public class ExceptionsHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(final ValidationException e) {
        log.error(e.getMessage());
        return new ErrorResponse("Ошибка валидации.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final EntityNotFoundException e) {
        log.error(e.getMessage());
        return new ErrorResponse("Объект не был найден.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleAlreadyExists(final EntityAlreadyExistsException e) {
        log.error(e.getMessage());
        return new ErrorResponse("Пользователь с таким логином или email уже существует.", e.getMessage());
    }
}
