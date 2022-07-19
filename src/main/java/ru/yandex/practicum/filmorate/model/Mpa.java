package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class Mpa {
    @Min(value = 1, message = "Mpa id must be between 1 and 5")
    @Max(value = 5, message = "Mpa id must be between 1 and 5")
    private int id;
    @NotBlank(message = "name can`t be empty")
    private String name;
}
