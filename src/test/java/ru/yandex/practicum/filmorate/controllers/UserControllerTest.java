package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserController userController;
    private User correctUser;
    private User userWithIncorrectEmail;
    private User userWithIncorrectLogin;
    private User userWithEmptyName;
    private User userWithIncorrectBirthday;

    @BeforeEach
    public void beforeEach() {
        FilmorateApplication.main(new String[0]);
        userController = new UserController();
        correctUser = new User("e5k4p3@gmail.com", "e5k4p3", "Mulenas", LocalDate.of(1995, 7, 11));
        userWithIncorrectEmail = new User("@gmail.com", "e5k4p3", "Mulenas", LocalDate.of(1995, 7, 11));
        userWithIncorrectLogin = new User("e5k4p3@gmail.com", "e5 k4 p3", "Mulenas", LocalDate.of(1995, 7, 11));
        userWithEmptyName = new User("e5k4p3@gmail.com", "e5k4p3", "", LocalDate.of(1995, 7, 11));
        userWithIncorrectBirthday = new User("e5k4p3@gmail.com", "e5k4p3", "Mulenas", LocalDate.of(2230, 7, 11));
    }

    @Test
    public void validationTest() throws ValidationException {
        userController.addUser(correctUser);
        assertEquals(1, userController.getUsers().size());
        assertThrows(ValidationException.class, () -> userController.addUser(userWithIncorrectEmail));
        assertThrows(ValidationException.class, () -> userController.addUser(userWithIncorrectLogin));
        assertThrows(ValidationException.class, () -> userController.addUser(userWithIncorrectBirthday));
        assertDoesNotThrow(() -> userController.addUser(userWithEmptyName));
        assertEquals(userWithEmptyName.getLogin(), userWithEmptyName.getName());
        assertEquals(2, userController.getUsers().size());
    }

}