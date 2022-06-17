package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private static int idCounter = 1;

    private int id;
    @Email(message = "Email неверного формата")
    private final String email;
    @NotBlank
    @Pattern(regexp = "\\S*", message = "Login не может сдежать пробелы")
    private final String login;
    private final String name;
    @PastOrPresent
    private final LocalDate birthday;
    private Set<Integer> friendsIdSet = new HashSet<>();

    public void generateId() {
        this.id = idCounter++;
    }

    public User checkUserName() {
        if (name == null || name.isBlank()) {
            return new User(email, login, login, birthday);
        }
        return this;
    }
}
