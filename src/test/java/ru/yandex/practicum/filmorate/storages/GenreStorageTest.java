package ru.yandex.practicum.filmorate.storages;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.JdbcH2Runner;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.dao.impl.GenreDbStorage;
import ru.yandex.practicum.filmorate.exceptionhandler.exceptions.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ContextConfiguration(classes = GenreDbStorage.class)
public class GenreStorageTest extends JdbcH2Runner {

    @Autowired
    private GenreStorage genreStorage;

    @Test
    @Sql({"classpath:schema.sql", "classpath:films-data.sql"})
    public void getGenreById() {
        assertEquals("Драма", genreStorage.getGenreById(2).getName());
        assertThrows(EntityNotFoundException.class, () -> genreStorage.getGenreById(9999));
    }

    @Test
    @Sql({"classpath:schema.sql", "classpath:films-data.sql"})
    public void getAllGenres() {
        assertEquals(6, genreStorage.getAllGenres().size());
    }
}
