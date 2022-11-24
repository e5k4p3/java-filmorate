package ru.yandex.practicum.filmorate.storages;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.JdbcH2Runner;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.exceptionhandler.exceptions.EntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptionhandler.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = UserDbStorage.class)
public class UserStorageTest extends JdbcH2Runner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserStorage userStorage;

    @Test
    @Sql({"classpath:schema.sql", "classpath:users-data.sql"})
    public void addUser() {
        User correctUser = new User("new@gmail.com", "User", "User",
                LocalDate.of(1995, 7, 11));
        userStorage.addUser(correctUser);
        String sqlQuery = "SELECT * FROM users_model WHERE email = 'new@gmail.com'";
        SqlRowSet userRow = jdbcTemplate.queryForRowSet(sqlQuery);
        userRow.next();
        User newUser = getUserFromRow(userRow);
        assertEquals(correctUser.getEmail(), newUser.getEmail());
        assertEquals(correctUser.getLogin(), newUser.getLogin());
        assertEquals(correctUser.getName(), newUser.getName());
        assertEquals(correctUser.getBirthday(), newUser.getBirthday());
        User duplicateUser = new User("e5k4p3@gmail.com", "e5k4p3", "e5k4p3",
                LocalDate.of(1995, 7, 11));
        assertThrows(EntityAlreadyExistsException.class, () -> userStorage.addUser(duplicateUser));
    }

    @Test
    @Sql({"classpath:schema.sql", "classpath:users-data.sql"})
    public void deleteUser() {
        userStorage.deleteUser(1);
        String sqlQuery = "SELECT * FROM users_model WHERE user_id = 1";
        SqlRowSet userRow = jdbcTemplate.queryForRowSet(sqlQuery);
        assertFalse(userRow.next());
        assertThrows(EntityNotFoundException.class, () -> userStorage.deleteUser(9999));
    }

    @Test
    @Sql({"classpath:schema.sql", "classpath:users-data.sql"})
    public void updateUser() {
        User newUser = new User("newuser@gmail.com", "NewUser", "NewUser",
                LocalDate.of(2000, 8, 20));
        newUser.setId(2);
        userStorage.updateUser(newUser);
        String sqlQuery = "SELECT * FROM users_model WHERE user_id = 2";
        SqlRowSet userRow = jdbcTemplate.queryForRowSet(sqlQuery);
        userRow.next();
        User user = getUserFromRow(userRow);
        assertEquals(newUser.getEmail(), user.getEmail());
        assertEquals(newUser.getLogin(), user.getLogin());
        assertEquals(newUser.getName(), user.getName());
        assertEquals(newUser.getBirthday(), user.getBirthday());
        newUser.setId(9999);
        assertThrows(EntityNotFoundException.class, () -> userStorage.updateUser(newUser));
    }

    @Test
    @Sql({"classpath:schema.sql", "classpath:users-data.sql"})
    public void getUserById() {
        User user = userStorage.getUserById(1);
        assertEquals(1, user.getId());
        assertThrows(EntityNotFoundException.class, () -> userStorage.getUserById(9999));
    }

    @Test
    @Sql({"classpath:schema.sql", "classpath:users-data.sql"})
    public void getAllUsers() {
        List<User> allUsers = userStorage.getAllUsers();
        assertEquals(5, allUsers.size());
    }

    private User getUserFromRow(SqlRowSet userRow) {
        User user = new User(userRow.getString("email"),
                userRow.getString("login"),
                userRow.getString("name"),
                userRow.getDate("birthday").toLocalDate());
        user.setId(userRow.getInt("user_id"));
        return user;
    }
}
