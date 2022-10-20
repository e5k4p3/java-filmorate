package ru.yandex.practicum.filmorate.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {
    private final MockMvc mockMvc;
    private Gson gson;
    private Film correctFilm;
    private Film filmWithEmptyName;
    private Film filmWithIncorrectDescription;
    private Film filmWithIncorrectReleaseDate;
    private Film filmWithIncorrectDuration;

    @Autowired
    FilmControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @BeforeEach
    public void beforeEach() {
        gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        correctFilm = new Film("Название фильма", "Описание фильма",
                LocalDate.of(2000, 10, 10), 100L);
        filmWithEmptyName = new Film("", "Описание фильма",
                LocalDate.of(2000, 10, 10), 100L);
        filmWithIncorrectDescription = new Film("Название фильма", String.copyValueOf(new char[201]),
                LocalDate.of(2000, 10, 10), 100L);
        filmWithIncorrectReleaseDate = new Film("Название фильма", "Описание фильма",
                LocalDate.of(1800, 10, 10), 100L);
        filmWithIncorrectDuration = new Film("Название фильма", "Описание фильма",
                LocalDate.of(2000, 10, 10), -100L);
    }

    @Test
    public void validationTest() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(correctFilm)))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(filmWithEmptyName)))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ValidationException));

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(filmWithIncorrectDescription)))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ValidationException));

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(filmWithIncorrectReleaseDate)))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ValidationException));

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(filmWithIncorrectDuration)))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ValidationException));
    }
}