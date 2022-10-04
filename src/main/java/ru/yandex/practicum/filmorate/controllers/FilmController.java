package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
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
    public Film addFilm(@Valid @RequestBody Film film, BindingResult bindingResult) throws ValidationException {
        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                log.warn(error.getDefaultMessage());
            }
            throw new ValidationException("Film не прошел валидацию.");
        }
        film.setId(getNewId());
        films.put(film.getId(), film);
        log.info("Добавлен фильм " + film.getName());
        return films.get(film.getId());
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film, BindingResult bindingResult) throws FilmNotFoundException, ValidationException {
        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                log.warn(error.getDefaultMessage());
            }
            throw new ValidationException("Film не прошел валидацию.");
        }
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Фильм с id " + film.getId() + " был изменен.");
        } else {
            throw new FilmNotFoundException("Фильм с id " + film.getId() + " не найден.");
        }
        return films.get(film.getId());
    }

    private int getNewId() {
        return id++;
    }
}
