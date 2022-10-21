package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptionhandler.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private int id = 1;
    private final Map<Integer, User> users = new HashMap<>();
    @Override
    public User addUser(User user) {
        checkUserName(user);
        user.setId(getNewId());
        users.put(user.getId(), user);
        log.info("Пользователь с логином " + user.getLogin() + " был сохранен.");
        return user;
    }

    @Override
    public void deleteUser(int id) {
        if (!users.containsKey(id)) {
            throw new EntityNotFoundException("Пользователь с id " + id + " не найден.");
        }
        users.remove(id);
        log.info("Пользователь с id " + id + " был удален.");
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new EntityNotFoundException("Пользователь с id " + user.getId() + " не найден.");
        }
        users.put(user.getId(), user);
        log.info("Пользователь с id " + user.getId() + " был обновлен.");
        return user;
    }

    @Override
    public User getUserById(int id) {
        if (!users.containsKey(id)) {
            throw new EntityNotFoundException("Пользователь с id " + id + " не найден.");
        }
        return users.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    private int getNewId() {
        return id++;
    }

    private void checkUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя с id " + user.getId() + " было изменено на его login.");
        }
    }
}
