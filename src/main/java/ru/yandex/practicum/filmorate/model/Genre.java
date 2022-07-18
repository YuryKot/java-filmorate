package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
public class Genre {
    @Positive
    private int id;
    @NotBlank(message = "name can`t be empty")
    private String name;
}
