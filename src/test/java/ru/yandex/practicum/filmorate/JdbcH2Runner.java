package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@JdbcTest
@ExtendWith(SpringExtension.class)
@PropertySource("classpath:application-test.properties")
public abstract class JdbcH2Runner {
}
