package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.exceptionhandler.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Slf4j
@Repository
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public Film addFilm(Film film) {
        String sqlQuery = "INSERT INTO films_model(title, description, release_date, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());
        addFilmGenres(film);
        log.info("Фильм с названием " + film.getName() + " добавлен.");
        return film;
    }

    @Override
    public void deleteFilm(int id) {
        if (getSqlRowSetByFilmId(id).next()) {
            String filmSqlQuery = "DELETE FROM films_model WHERE film_id = ?";
            jdbcTemplate.update(filmSqlQuery, id);
            removeFilmGenres(id);
            log.info("Фильм с id " + id + " удален.");
        } else {
            throw new EntityNotFoundException("Фильм с id " + id + " не найден.");
        }
    }

    @Override
    public Film updateFilm(Film film) {
        if (getSqlRowSetByFilmId(film.getId()).next()) {
            String sqlQuery = "UPDATE films_model SET title = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                    "WHERE film_id = ?";
            jdbcTemplate.update(sqlQuery,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId());
            removeFilmGenres(film.getId());
            addFilmGenres(film);
            film.setGenres(getFilmGenres(film.getId()));
            log.info("Фильм с id " + film.getId() + " обновлен.");
            return film;
        } else {
            throw new EntityNotFoundException("Фильм с id " + film.getId() + " не найден.");
        }
    }

    @Override
    public Film getFilmById(int id) {
        SqlRowSet filmRow = getSqlRowSetByFilmId(id);
        if (filmRow.next()) {
            return getFilmWithId(filmRow);
        } else {
            throw new EntityNotFoundException("Фильм с id " + id + " не найден.");
        }
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> allFilms = new ArrayList<>();
        String sqlQuery = "SELECT * FROM films_model";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQuery);
        while (filmRows.next()) {
            allFilms.add(getFilmWithId(filmRows));
        }
        return allFilms;
    }

    public List<Film> getMostLikedFilms(int limit) {
        List<Film> topFilms = new ArrayList<>();
        String sqlQuery = "SELECT * FROM films_model LEFT JOIN films_likes fl ON films_model.film_id = fl.film_id " +
                "GROUP BY TITLE ORDER BY COUNT(fl.FILM_ID) DESC LIMIT ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQuery, limit);
        while (filmRows.next()) {
            topFilms.add(getFilmWithId(filmRows));
        }
        return topFilms;
    }

    private SqlRowSet getSqlRowSetByFilmId(int id) {
        String sqlQuery = "SELECT * FROM films_model WHERE film_id = ?";
        return jdbcTemplate.queryForRowSet(sqlQuery, id);
    }

    private Film getFilmWithId(SqlRowSet filmRow) {
        Film film = new Film(filmRow.getString("title"),
                filmRow.getString("description"),
                filmRow.getDate("release_date").toLocalDate(),
                filmRow.getLong("duration"),
                getFilmMpa(filmRow));
        film.setId(filmRow.getInt("film_id"));
        film.setGenres(getFilmGenres(film.getId()));
        return film;
    }

    private Mpa getFilmMpa(SqlRowSet filmRow) {
        String sqlQuery = "SELECT * FROM mpa_dictionary WHERE mpa_id = ?";
        SqlRowSet mpaRow = jdbcTemplate.queryForRowSet(sqlQuery, filmRow.getInt("mpa_id"));
        if (mpaRow.next()) {
            return new Mpa(mpaRow.getInt("mpa_id"),
                    mpaRow.getString("rating"),
                    mpaRow.getString("rating_description"));
        } else {
            throw new EntityNotFoundException("MPA с id " + mpaRow.getInt("mpa_id") + " не найден.");
        }
    }

    private Set<Genre> getFilmGenres(int id) {
        Set<Genre> filmGenres = new TreeSet<>(Comparator.comparingInt(Genre::getId));
        String sqlQuery = "SELECT * FROM genre_dictionary WHERE genre_id IN " +
                "(SELECT genre_id FROM FILMS_GENRES WHERE film_id = ?)";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        while (genreRows.next()) {
            filmGenres.add(getGenreFromRow(genreRows));
        }
        return filmGenres;
    }

    private Genre getGenreFromRow(SqlRowSet genreRow) {
        return new Genre(genreRow.getInt("genre_id"),
                genreRow.getString("genre_name"),
                genreRow.getString("genre_description"));
    }

    private void addFilmGenres(Film film) {
        checkGenreIdExistence(film);
        String sqlQuery = "INSERT INTO films_genres(film_id, genre_id) VALUES (?, ?)";
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(sqlQuery, film.getId(), genre.getId());
        }
    }

    private void removeFilmGenres(int id) {
        String sqlQuery = "DELETE FROM films_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    private void checkGenreIdExistence(Film film) {
        String sqlQuery = "SELECT * FROM genre_dictionary WHERE genre_id = ?";
        for (Genre genre : film.getGenres()) {
            if (!jdbcTemplate.queryForRowSet(sqlQuery, genre.getId()).next()) {
                throw new EntityNotFoundException("Жанр с id " + genre.getId() + " не найден.");
            }
        }
    }
}
