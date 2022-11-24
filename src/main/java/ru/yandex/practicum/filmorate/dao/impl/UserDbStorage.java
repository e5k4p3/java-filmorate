package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exceptionhandler.exceptions.EntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptionhandler.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        checkUserName(user);
        try {
            String sqlQuery = "INSERT INTO users_model(email, login, name, birthday) VALUES (?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
                PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"user_id"});
                stmt.setString(1, user.getEmail());
                stmt.setString(2, user.getLogin());
                stmt.setString(3, user.getName());
                stmt.setDate(4, Date.valueOf(user.getBirthday()));
                return stmt;
            }, keyHolder);
            user.setId(keyHolder.getKey().intValue());
            log.info("Пользователь с логином " + user.getLogin() + " добавлен.");
            return user;
        } catch (DuplicateKeyException e) {
            throw new EntityAlreadyExistsException("Такой пользователь уже существует.");
        }
    }

    @Override
    public void deleteUser(int id) {
        if (getSqlRowSetByUserId(id).next()) {
            String sqlQuery = "DELETE FROM users_model WHERE user_id = ?";
            jdbcTemplate.update(sqlQuery, id);
            log.info("Пользователь с id " + id + " удален.");
        } else {
            throw new EntityNotFoundException("Пользователь с id " + id + " не найден.");
        }
    }

    @Override
    public User updateUser(User user) {
        if (getSqlRowSetByUserId(user.getId()).next()) {
            String sqlQuery = "UPDATE users_model SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
            checkUserName(user);
            jdbcTemplate.update(sqlQuery,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday(),
                    user.getId());
            log.info("Пользователь с id " + user.getId() + " обновлен.");
            return user;
        } else {
            throw new EntityNotFoundException("Пользователь с id " + user.getId() + " не найден.");
        }
    }

    @Override
    public User getUserById(int id) {
        SqlRowSet userRow = getSqlRowSetByUserId(id);
        if (userRow.next()) {
            return getUserFromRow(userRow);
        } else {
            throw new EntityNotFoundException("Пользователь с id " + id + " не найден.");
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> allUsers = new ArrayList<>();
        String sqlQuery = "SELECT * FROM users_model";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery);
        while (userRows.next()) {
            allUsers.add(getUserFromRow(userRows));
        }
        return allUsers;
    }

    private void checkUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private SqlRowSet getSqlRowSetByUserId(int id) {
        String sqlQuery = "SELECT * FROM users_model WHERE user_id = ?";
        return jdbcTemplate.queryForRowSet(sqlQuery, id);
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
