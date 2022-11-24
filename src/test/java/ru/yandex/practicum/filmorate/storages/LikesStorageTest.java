package ru.yandex.practicum.filmorate.storages;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.JdbcH2Runner;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.LikesStorage;
import ru.yandex.practicum.filmorate.dao.MpaStorage;
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.LikesDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.MpaDbStorage;
import ru.yandex.practicum.filmorate.exceptionhandler.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ContextConfiguration(classes = {LikesDbStorage.class,
        FilmDbStorage.class,
        MpaDbStorage.class})
public class LikesStorageTest extends JdbcH2Runner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private LikesStorage likesStorage;

    @Autowired
    private FilmStorage filmStorage;

    @Autowired
    private MpaStorage mpaStorage;

    private Film film;
    private Mpa gMpa;

    @BeforeEach
    public void beforeEach() {
        gMpa = mpaStorage.getMpaById(1);
        film = new Film("Название", "Описание",
                LocalDate.of(2000, 1, 1), 30L, gMpa);
    }

    @Test
    @Sql({"classpath:schema.sql", "classpath:users-data.sql"})
    public void addLikeToFilm() {
        int id = filmStorage.addFilm(film).getId();
        likesStorage.addLikeToFilm(id, 1);
        String sqlQuery = "SELECT COUNT(like_id) AS amount FROM films_likes WHERE film_id = ?";
        SqlRowSet likeRow = jdbcTemplate.queryForRowSet(sqlQuery, id);
        likeRow.next();
        assertEquals(1, likeRow.getInt("amount"));
        assertThrows(EntityNotFoundException.class, () -> likesStorage.addLikeToFilm(9999, 1));
        assertThrows(EntityNotFoundException.class, () -> likesStorage.addLikeToFilm(1, 9999));
    }

    @Test
    @Sql({"classpath:schema.sql", "classpath:users-data.sql"})
    public void removeLikeFromFilm() {
        int id = filmStorage.addFilm(film).getId();
        likesStorage.addLikeToFilm(id, 1);
        String sqlQuery = "SELECT COUNT(like_id) AS amount FROM films_likes WHERE film_id = ?";
        SqlRowSet likeRow = jdbcTemplate.queryForRowSet(sqlQuery, id);
        likeRow.next();
        assertEquals(1, likeRow.getInt("amount"));
        likesStorage.removeLikeFromFilm(id, 1);
        SqlRowSet newLikeRow = jdbcTemplate.queryForRowSet(sqlQuery, id);
        newLikeRow.next();
        assertEquals(0, newLikeRow.getInt("amount"));
        assertThrows(EntityNotFoundException.class, () -> likesStorage.removeLikeFromFilm(9999, 1));
        assertThrows(EntityNotFoundException.class, () -> likesStorage.removeLikeFromFilm(1, 9999));
    }
}
