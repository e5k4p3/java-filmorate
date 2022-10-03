package ru.yandex.practicum.filmorate.validators;

import org.slf4j.Logger;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class FilmValidator {
    private FilmValidator(){}

    public static boolean validateFilm(Film film, Logger log) {
        boolean isValidated = true; // решил сделать так, потому что хотел отобразить в логах все ошибки, а не обрываться на первой ошибке
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Название фильма пустое.");
            isValidated = false;
        }
        if (film.getDescription().toCharArray().length > 200) {
            log.error("Описание фильма больше 200 символов.");
            isValidated = false;
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Дата релиза фильма раньше 28 декабря 1895 года.");
            isValidated = false;
        }
        if (film.getDuration() < 0) {
            log.error("Продожительность фильма меньше нуля.");
            isValidated = false;
        }
        return isValidated;
    }
}
