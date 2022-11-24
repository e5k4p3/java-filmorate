package ru.yandex.practicum.filmorate.storages;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.FriendsStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exceptionhandler.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FriendsStorageTest {

    @Autowired
    private UserStorage userStorage;

    @Autowired
    private FriendsStorage friendsStorage;

    @Test
    public void addToFriends() {
        friendsStorage.addToFriends(1, 2);
        assertEquals(1, friendsStorage.getUserFriends(1).size());
        assertThrows(EntityNotFoundException.class, () -> friendsStorage.addToFriends(1, 9999));
    }

    @Test
    public void removeFromFriends() {
        friendsStorage.addToFriends(1, 2);
        assertEquals(1, friendsStorage.getUserFriends(1).size());
        friendsStorage.removeFromFriends(1, 2);
        assertEquals(0, friendsStorage.getUserFriends(1).size());
        assertThrows(EntityNotFoundException.class, () -> friendsStorage.removeFromFriends(1, 9999));
    }

    @Test
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
    public void getCommonFriends() {
        friendsStorage.addToFriends(1, 2);
        friendsStorage.addToFriends(3, 2);
        List<User> commonFriends = new ArrayList<>();
        commonFriends.add(userStorage.getUserById(2));
        assertEquals(friendsStorage.getCommonFriends(1, 3), commonFriends);
        assertThrows(EntityNotFoundException.class, () -> friendsStorage.getCommonFriends(1, 9999));
    }
}
