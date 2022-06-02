package ru.yandex.practicum.filmorate.model;

import ru.yandex.practicum.filmorate.controller.FilmController;

import java.time.Duration;
import java.time.LocalDate;

public class test {
    public static void main(String[] args) {

        Film f = new Film(null, "q".repeat(250), LocalDate.now(), -3);
        FilmController controller = new FilmController();
        //Film f = new Film("name", "descr", LocalDate.now(), 0);
        //Film returnFilm = controller.addFilm(f);


    }
}
