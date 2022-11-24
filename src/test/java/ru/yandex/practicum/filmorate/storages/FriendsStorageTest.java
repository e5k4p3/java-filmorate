package ru.yandex.practicum.filmorate.storages;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.JdbcH2Runner;
import ru.yandex.practicum.filmorate.dao.FriendsStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.dao.impl.FriendsDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.exceptionhandler.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ContextConfiguration(classes = {FriendsDbStorage.class,
        UserDbStorage.class})
public class FriendsStorageTest extends JdbcH2Runner {

    @Autowired
    private UserStorage userStorage;

    @Autowired
    private FriendsStorage friendsStorage;

    @Test
    @Sql({"classpath:schema.sql", "classpath:friends-data.sql"})
    public void addToFriends() {
        friendsStorage.addToFriends(1, 2);
        assertEquals(1, friendsStorage.getUserFriends(1).size());
        assertThrows(EntityNotFoundException.class, () -> friendsStorage.addToFriends(1, 9999));
    }

    @Test
    @Sql({"classpath:schema.sql", "classpath:friends-data.sql"})
    public void removeFromFriends() {
        friendsStorage.addToFriends(1, 2);
        assertEquals(1, friendsStorage.getUserFriends(1).size());
        friendsStorage.removeFromFriends(1, 2);
        assertEquals(0, friendsStorage.getUserFriends(1).size());
        assertThrows(EntityNotFoundException.class, () -> friendsStorage.removeFromFriends(1, 9999));
    }

    @Test
    @Sql({"classpath:schema.sql", "classpath:friends-data.sql"})
    public void getUserFriends() {
        friendsStorage.addToFriends(1, 2);
        friendsStorage.addToFriends(1, 3);
        List<User> userFriends = new ArrayList<>();
        userFriends.add(userStorage.getUserById(2));
        userFriends.add(userStorage.getUserById(3));
        assertEquals(friendsStorage.getUserFriends(1), userFriends);
        assertThrows(EntityNotFoundException.class, () -> friendsStorage.getUserFriends(9999));
    }

    @Test
    @Sql({"classpath:schema.sql", "classpath:friends-data.sql"})
    public void getCommonFriends() {
        friendsStorage.addToFriends(1, 2);
        friendsStorage.addToFriends(3, 2);
        List<User> commonFriends = new ArrayList<>();
        commonFriends.add(userStorage.getUserById(2));
        assertEquals(friendsStorage.getCommonFriends(1, 3), commonFriends);
        assertThrows(EntityNotFoundException.class, () -> friendsStorage.getCommonFriends(1, 9999));
    }
}
