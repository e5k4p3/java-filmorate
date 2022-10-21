package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import ru.yandex.practicum.filmorate.exceptionhandler.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptionhandler.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public void deleteUser(int id) {
        userStorage.deleteUser(id);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User getUserById(int id) {
        return userStorage.getUserById(id);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void addFriend(int userId, int friendId) {
        if (userStorage.getUserById(userId) == null) {
            throw new UserNotFoundException("Пользователь с id " + userId + " не найден.");
        }
        if (userStorage.getUserById(friendId) == null) {
            throw new UserNotFoundException("Пользователь с id " + userId + " не найден.");
        }
        userStorage.getUserById(userId).addFriend(friendId);
        userStorage.getUserById(friendId).addFriend(userId);
    }

    public void removeFriend(int userId, int friendId) {
        if (userStorage.getUserById(userId) == null) {
            throw new UserNotFoundException("Пользователь с id " + userId + " не найден.");
        }
        if (userStorage.getUserById(friendId) == null) {
            throw new UserNotFoundException("Пользователь с id " + userId + " не найден.");
        }
        userStorage.getUserById(userId).removeFriend(friendId);
        userStorage.getUserById(friendId).removeFriend(userId);
    }

    public List<User> getUserFriends(int userId) {
        return userStorage.getUserById(userId).getFriends().stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int userId, int friendId) {
        return userStorage.getUserById(userId).getFriends().stream()
                .filter(userStorage.getUserById(friendId).getFriends()::contains)
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public void logValidationErrors(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                log.warn(error.getDefaultMessage());
                throw new ValidationException("User не прошел валидацию.");
            }
        }
    }
}
