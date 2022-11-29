package ru.yandex.practicum.filmorate.storages;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.exceptionhandler.exceptions.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class GenreStorageTest {

    @Autowired
    private GenreStorage genreStorage;

    @Test
    public void getGenreById() {
        assertEquals("Драма", genreStorage.getGenreById(2).getName());
        assertThrows(EntityNotFoundException.class, () -> genreStorage.getGenreById(9999));
    }

    @Test
    public void getAllGenres() {
        assertEquals(6, genreStorage.getAllGenres().size());
    }
}
