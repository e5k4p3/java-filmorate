package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController filmController;
    private Film correctFilm;
    private Film filmWithEmptyName;
    private Film filmWithIncorrectDescription;
    private Film filmWithIncorrectReleaseDate;
    private Film filmWithIncorrectDuration;

    @BeforeEach
    public void beforeEach() {
        filmController = new FilmController();
        correctFilm = new Film("Название фильма", "Описание фильма",
                LocalDate.of(2000, 10, 10), 100L);
        filmWithEmptyName = new Film("", "Описание фильма",
                LocalDate.of(2000, 10, 10), 100L);
        filmWithIncorrectDescription = new Film("Название фильма", String.copyValueOf(new char[201]),
                LocalDate.of(2000, 10, 10), 100L);
        filmWithIncorrectReleaseDate = new Film("Название фильма", "Описание фильма",
                LocalDate.of(1800, 10, 10), 100L);
        filmWithIncorrectDuration = new Film("Название фильма", "Описание фильма",
                LocalDate.of(2000, 10, 10), -100L);
    }

    @Test
    public void validationTest() throws ValidationException {
        filmController.addFilm(correctFilm);
        assertEquals(1, filmController.getFilms().size());
        assertThrows(ValidationException.class, () -> filmController.addFilm(filmWithEmptyName));
        assertThrows(ValidationException.class, () -> filmController.addFilm(filmWithIncorrectDescription));
        assertThrows(ValidationException.class, () -> filmController.addFilm(filmWithIncorrectReleaseDate));
        assertThrows(ValidationException.class, () -> filmController.addFilm(filmWithIncorrectDuration));
        assertEquals(1, filmController.getFilms().size());
    }
}