package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@JdbcTest
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application-test.properties")
public abstract class JdbcH2Runner {
}
