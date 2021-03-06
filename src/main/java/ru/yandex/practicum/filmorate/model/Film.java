package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {

    private int id;
    @NotBlank(message = "name can`t be empty")
    private final String name;
    @Size(max = 200)
    private final String description;
    private final LocalDate releaseDate;
    @Positive
    private final int duration;
    @NotNull(message = "mpa can`t be null")
    private Mpa mpa;
    private Set<Integer> usersIdLikesSet = new HashSet<>();
    private Set<Genre> genres = new HashSet<>();

    public void validateFilm() {
        if (releaseDate.isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }
}
