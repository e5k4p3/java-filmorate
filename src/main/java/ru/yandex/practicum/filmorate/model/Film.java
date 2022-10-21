package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.validators.ReleaseDateValidation;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private int id;
    @NotBlank(message = "Название фильма не может быть пустым.")
    private final String name;
    @NotNull(message = "Описание фильма не может быть null.")
    @Size(max = 200, message = "Длина описание не может быть больше 200 символов.")
    private final String description;
    @NotNull(message = "Дата выхода фильма не может быть null.")
    @ReleaseDateValidation
    private final LocalDate releaseDate;
    @NotNull(message = "Длительность фильма не может быть null.")
    @Positive(message = "Длительность фильма не может быть отрицательной.")
    private final Long duration;
    private final Set<Integer> likes = new HashSet<>();

    public void addLike(int id) {
        likes.add(id);
    }

    public void removeLike(int id) {
        likes.remove(id);
    }

    public Set<Integer> getLikes() {
        return likes;
    }

    public int getLikesAmount() {
        return likes.size();
    }
}
