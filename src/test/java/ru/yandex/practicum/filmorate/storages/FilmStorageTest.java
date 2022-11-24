package ru.yandex.practicum.filmorate.storages;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.JdbcH2Runner;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.dao.impl.*;
import ru.yandex.practicum.filmorate.exceptionhandler.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = {FilmDbStorage.class,
        GenreDbStorage.class,
        MpaDbStorage.class,
        UserDbStorage.class,
        LikesDbStorage.class})
public class FilmStorageTest extends JdbcH2Runner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FilmStorage filmStorage;

    @Autowired
    private GenreStorage genreStorage;

    @Autowired
    private MpaStorage mpaStorage;

    @Autowired
    private UserStorage userStorage;

    @Autowired
    private LikesStorage likesStorage;

    private Film film;
    private Genre genreComedy;
    private Genre genreDrama;
    private Mpa gMpa;
    private Mpa pgMpa;

    @BeforeEach
    public void beforeEach() {
        genreComedy = genreStorage.getGenreById(1);
        genreDrama = genreStorage.getGenreById(2);
        gMpa = mpaStorage.getMpaById(1);
        pgMpa = mpaStorage.getMpaById(2);
        film = new Film("Название", "Описание",
                LocalDate.of(2000, 1, 1), 30L, gMpa);
    }

    @Test
    @Sql({"classpath:schema.sql", "classpath:films-data.sql"})
    public void addFilm() {
        Set<Genre> allGenres = new TreeSet<>(Comparator.comparing(Genre::getId));
        allGenres.add(genreComedy);
        allGenres.add(genreDrama);
        film.setGenres(allGenres);
        int id = filmStorage.addFilm(film).getId();
        String sqlQuery = "SELECT * FROM films_model WHERE film_id = ?";
        SqlRowSet filmRow = jdbcTemplate.queryForRowSet(sqlQuery, id);
        filmRow.next();
        Film newFilm = getFilmFromRow(filmRow);
        assertEquals(film.getName(), newFilm.getName());
        assertEquals(film.getDescription(), newFilm.getDescription());
        assertEquals(film.getReleaseDate(), newFilm.getReleaseDate());
        assertEquals(film.getDuration(), newFilm.getDuration());
        assertEquals(film.getMpa(), newFilm.getMpa());
        Set<Genre> newAllGenres = newFilm.getGenres();
        assertEquals(allGenres, newAllGenres);
    }

    @Test
    @Sql({"classpath:schema.sql", "classpath:films-data.sql"})
    public void deleteFilm() {
        int id = filmStorage.addFilm(film).getId();
        filmStorage.deleteFilm(id);
        String sqlQuery = "SELECT * FROM films_model WHERE film_id = ?";
        SqlRowSet filmRow = jdbcTemplate.queryForRowSet(sqlQuery, id);
        assertFalse(filmRow.next());
        assertThrows(EntityNotFoundException.class, () -> filmStorage.deleteFilm(9999));
    }

    @Test
    @Sql({"classpath:schema.sql", "classpath:films-data.sql"})
    public void updateFilm() {
        Film newFilm = new Film("Новое название", "Новое описание",
                LocalDate.of(2005, 12, 12), 60L, pgMpa);
        Set<Genre> allGenres = new TreeSet<>(Comparator.comparing(Genre::getId));
        allGenres.add(genreComedy);
        newFilm.setGenres(allGenres);
        int id = filmStorage.addFilm(film).getId();
        newFilm.setId(id);
        filmStorage.updateFilm(newFilm);
        String sqlQuery = "SELECT * FROM films_model WHERE film_id = ?";
        SqlRowSet updatedFilmRow = jdbcTemplate.queryForRowSet(sqlQuery, id);
        updatedFilmRow.next();
        Film updatedFilm = getFilmFromRow(updatedFilmRow);
        assertNotEquals(film.getName(), updatedFilm.getName());
        assertNotEquals(film.getDescription(), updatedFilm.getDescription());
        assertNotEquals(film.getReleaseDate(), updatedFilm.getReleaseDate());
        assertNotEquals(film.getMpa(), updatedFilm.getMpa());
        assertNotEquals(film.getGenres(), updatedFilm.getGenres());
        newFilm.setId(9999);
        assertThrows(EntityNotFoundException.class, () -> filmStorage.updateFilm(newFilm));
    }

    @Test
    @Sql({"classpath:schema.sql", "classpath:films-data.sql"})
    public void getFilmById() {
        filmStorage.addFilm(film);
        Film film = filmStorage.getFilmById(1);
        assertEquals(1, film.getId());
        assertThrows(EntityNotFoundException.class, () -> filmStorage.getFilmById(9999));
    }

    @Test
    @Sql({"classpath:schema.sql", "classpath:films-data.sql"})
    public void getAllFilms() {
        filmStorage.addFilm(film);
        filmStorage.addFilm(film);
        filmStorage.addFilm(film);
        assertEquals(3, filmStorage.getAllFilms().size());
    }

    @Test
    @Sql({"classpath:schema.sql", "classpath:films-data.sql"})
    public void getMostLikedFilms() {
        User firstUser = userStorage.addUser(new User("e5k4p3@gmail.com", "e5k4p3", "e5k4p3",
                LocalDate.of(1995, 7, 11)));
        User secondUser = userStorage.addUser(new User("mulenas@gmail.com", "Mulenas", "Mulenas",
                LocalDate.of(1995, 7, 11)));
        Film secondFilm = new Film("Второй", "Описание второго",
                LocalDate.of(1999, 8, 15), 50L, gMpa);
        Film thirdFilm = new Film("Третий", "Описание третьего",
                LocalDate.of(2007, 4, 7), 50L, pgMpa);
        filmStorage.addFilm(film);
        int secondFilmId = filmStorage.addFilm(secondFilm).getId();
        int thirdFilmId = filmStorage.addFilm(thirdFilm).getId();
        likesStorage.addLikeToFilm(thirdFilmId, firstUser.getId());
        likesStorage.addLikeToFilm(thirdFilmId, secondUser.getId());
        likesStorage.addLikeToFilm(secondFilmId, firstUser.getId());
        List<Film> topFilms = filmStorage.getMostLikedFilms(10);
        System.out.println(topFilms.size());
        System.out.println(topFilms);
        assertEquals("Третий", topFilms.get(0).getName());
        assertEquals("Второй", topFilms.get(1).getName());
        assertEquals("Название", topFilms.get(2).getName());
    }

    private Film getFilmFromRow(SqlRowSet filmRow) {
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
}
