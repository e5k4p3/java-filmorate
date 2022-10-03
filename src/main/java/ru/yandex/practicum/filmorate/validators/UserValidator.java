package ru.yandex.practicum.filmorate.validators;

import org.slf4j.Logger;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserValidator {
    private UserValidator(){}

    public static boolean validateUser(User user, Logger log) {
        boolean isValidated = true; // решил сделать так, потому что хотел отобразить в логах все ошибки, а не обрываться на первой ошибке
        final String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        final Pattern pattern = Pattern.compile(regexPattern);
        final Matcher matcher = pattern.matcher(user.getEmail());
        if (!matcher.matches()) {
            log.error("Email не прошел валидацию.");
            isValidated = false;
        }
        if (user.getLogin() == null || user.getLogin().contains(" ") || user.getLogin().isBlank()) {
            log.error("Login не прошел валидацию.");
            isValidated = false;
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Name у пользователя " + user.getLogin() + " был изменен на его login.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Birthday не прошел валидацию.");
            isValidated = false;
        }
        return isValidated;
    }
}
