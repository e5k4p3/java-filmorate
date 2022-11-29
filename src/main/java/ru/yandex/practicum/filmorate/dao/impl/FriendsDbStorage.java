package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FriendsStorage;
import ru.yandex.practicum.filmorate.exceptionhandler.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class FriendsDbStorage implements FriendsStorage {
    JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendsDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addToFriends(int userId, int friendId) {
        checkUserExistence(userId);
        checkUserExistence(friendId);
        String sqlQuery = "INSERT INTO users_friends (user_id, user_friend_id) " +
                "VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, friendId);
        log.info("Пользователь с id " + userId + " добавил в друзья пользователя с id " + friendId + ".");
    }

    @Override
    public void removeFromFriends(int userId, int friendId) {
        checkUserExistence(userId);
        checkUserExistence(friendId);
        String sqlQuery = "DELETE FROM users_friends WHERE user_id = ? AND user_friend_id = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
        log.info("Пользователь с id " + userId + " удалил из друзей пользователя с id " + friendId + ".");
    }

    @Override
    public List<User> getUserFriends(int id) {
        checkUserExistence(id);
        List<User> userFriends = new ArrayList<>();
        String sqlQuery = "SELECT * FROM users_model " +
                "WHERE user_id IN (SELECT user_friend_id FROM users_friends WHERE user_id = ?)";
        SqlRowSet userFriendsRow = jdbcTemplate.queryForRowSet(sqlQuery, id);
        while (userFriendsRow.next()) {
            userFriends.add(getUserWithId(userFriendsRow));
        }
        return userFriends;
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        checkUserExistence(userId);
        checkUserExistence(otherId);
        List<User> commonFriends = new ArrayList<>();
        String sqlQuery = "SELECT * FROM users_model " +
                "WHERE user_id IN (SELECT user_friend_id FROM users_friends WHERE user_id = ? " +
                "INTERSECT SELECT user_friend_id FROM users_friends WHERE user_id = ?)";
        SqlRowSet commonFriendsRow = jdbcTemplate.queryForRowSet(sqlQuery, userId, otherId);
        while (commonFriendsRow.next()) {
            commonFriends.add(getUserWithId(commonFriendsRow));
        }
        return commonFriends;
    }

    private SqlRowSet getSqlRowSetByUserId(int id) {
        String sqlQuery = "SELECT * FROM users_model WHERE user_id = ?";
        return jdbcTemplate.queryForRowSet(sqlQuery, id);
    }

    private User getUserWithId(SqlRowSet userRow) {
        User user = new User(userRow.getString("email"),
                userRow.getString("login"),
                userRow.getString("name"),
                userRow.getDate("birthday").toLocalDate());
        user.setId(userRow.getInt("user_id"));
        return user;
    }

    private void checkUserExistence(int id) {
        if (!getSqlRowSetByUserId(id).next()) {
            throw new EntityNotFoundException("Пользователь с " + id + " не найден.");
        }
    }
}
