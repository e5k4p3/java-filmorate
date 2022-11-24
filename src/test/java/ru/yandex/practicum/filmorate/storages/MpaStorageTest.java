package ru.yandex.practicum.filmorate.storages;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.JdbcH2Runner;
import ru.yandex.practicum.filmorate.dao.MpaStorage;
import ru.yandex.practicum.filmorate.dao.impl.MpaDbStorage;
import ru.yandex.practicum.filmorate.exceptionhandler.exceptions.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ContextConfiguration(classes = MpaDbStorage.class)
public class MpaStorageTest extends JdbcH2Runner {

    @Autowired
    private MpaStorage mpaStorage;

    @Test
    @Sql({"classpath:schema.sql", "classpath:films-data.sql"})
    public void getMpaById() {
        assertEquals("PG", mpaStorage.getMpaById(2).getName());
        assertThrows(EntityNotFoundException.class, () -> mpaStorage.getMpaById(9999));
    }

    @Test
    @Sql({"classpath:schema.sql", "classpath:films-data.sql"})
    public void getAllMpa() {
        assertEquals(5, mpaStorage.getAllMpa().size());
    }
}
