package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validators.UserValidator;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private int id = 1;
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        return Collections.unmodifiableCollection(users.values());
    }

    @PostMapping
    public User addUser(@RequestBody User user) throws ValidationException {
        if (!UserValidator.validateUser(user, log)) {
            throw new ValidationException("User не прошел валидацию.");
        }
        user.setId(getNewId());
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @PutMapping
    public User updateUser(@RequestBody User user) throws ValidationException, UserNotFoundException {
        if (!UserValidator.validateUser(user, log)) {
            throw new ValidationException("User не прошел валидацию.");
        }
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        } else {
            throw new UserNotFoundException("Пользователь с id " + user.getId() + " не найден.");
        }
        return users.get(user.getId());
    }

    private int getNewId() {
        return id++;
    }
}
