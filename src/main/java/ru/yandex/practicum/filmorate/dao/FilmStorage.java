package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film addFilm(Film film);
    void deleteFilm(int id);
    Film updateFilm(Film film);
    Film getFilmById(int id);
    List<Film> getAllFilms();
    List<Film> getMostLikedFilms(int limit);
}
