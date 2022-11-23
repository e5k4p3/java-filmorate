package ru.yandex.practicum.filmorate.dao;

public interface LikesStorage {
    void addLikeToFilm(int filmId, int userId);
    void removeLikeFromFilm(int filmId, int userId);
}
