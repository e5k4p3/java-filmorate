package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import ru.yandex.practicum.filmorate.exceptionhandler.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exceptionhandler.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public void deleteFilm(int id) {
        filmStorage.deleteFilm(id);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public List<Film> getMostLikedFilms(int limit) {
        return filmStorage.getMostLikedFilms(limit);
    }

    public void addLikeToFilm(int filmId, int userId) {
        filmStorage.getFilmById(filmId).addLike(userId);
        log.info("Пользователь с id " + userId + " добавил лайк фильму с id " + filmId + ".");
    }

    public void removeLikeFromFilm(int filmId, int userId) {
        if (!filmStorage.getFilmById(filmId).getLikes().contains(userId)) {
            throw new EntityNotFoundException("У фильма с id " + filmId + " нету лайка от пользователя с id " + userId + ".");
        }
        filmStorage.getFilmById(filmId).removeLike(userId);
        log.info("Пользователь с id " + userId + " убрал лайк у фильма с id " + filmId + ".");
    }

    public void logValidationErrors(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                log.warn(error.getDefaultMessage());
                throw new ValidationException("Film не прошел валидацию.");
            }
        }
    }
}
