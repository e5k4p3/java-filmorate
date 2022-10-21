package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptionhandler.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private int id = 1;
    Map<Integer, Film> films = new HashMap<>();

    @Override
    public Film addFilm(Film film) {
        film.setId(getNewId());
        films.put(film.getId(), film);
        log.info("Фильм с названием " + film.getName() + " был сохранен.");
        return film;
    }

    @Override
    public void deleteFilm(int id) {
        if (!films.containsKey(id)) {
            throw new FilmNotFoundException("Фильм с id " + id + " не найден.");
        }
        films.remove(id);
        log.info("Фильм с id " + id + " был удален.");
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new FilmNotFoundException("Фильм с id " + film.getId() + " не найден.");
        }
        films.put(film.getId(), film);
        log.info("Фильм с id " + film.getId() + " был обновлен.");
        return film;
    }

    @Override
    public Film getFilmById(int id) {
        if (!films.containsKey(id)) {
            throw new FilmNotFoundException("Фильм с id " + id + " не найден.");
        }
        return films.get(id);
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public List<Film> getMostLikedFilms(int limit) {
        return films.values().stream()
                .sorted(Comparator.comparing(Film::getLikesAmount).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    private int getNewId() {
        return id++;
    }
}
