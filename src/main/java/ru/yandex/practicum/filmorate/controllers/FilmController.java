package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private int id = 1;
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getFilms() {
        return Collections.unmodifiableCollection(films.values());
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) throws ValidationException {
        if (!FilmValidator.validateFilm(film, log)) {
            throw new ValidationException("Film не прошел валидацию.");
        }
        film.setId(getNewId());
        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) throws ValidationException, FilmNotFoundException {
        if (!FilmValidator.validateFilm(film, log)) {
            throw new ValidationException("Film не прошел валидацию.");
        }
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        } else {
            throw new FilmNotFoundException("Фильм с id " + film.getId() + " не найден.");
        }
        return films.get(film.getId());
    }

    private int getNewId() {
        return id++;
    }
}
