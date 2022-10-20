package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
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
    public User addUser(@Valid @RequestBody User user, BindingResult bindingResult) throws ValidationException {
        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                log.warn(error.getDefaultMessage());
            }
            throw new ValidationException("User не прошел валидацию.");
        }
        user.setId(getNewId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Добавлен пользователь с логином " + user.getLogin());
        return users.get(user.getId());
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user, BindingResult bindingResult) throws UserNotFoundException, ValidationException {
        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                log.warn(error.getDefaultMessage());
            }
            throw new ValidationException("User не прошел валидацию.");
        }
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("Пользователь с id " + user.getId() + " был изменен.");
        } else {
            throw new UserNotFoundException("Пользователь с id " + user.getId() + " не найден.");
        }
        return users.get(user.getId());
    }

    private int getNewId() {
        return id++;
    }
}
