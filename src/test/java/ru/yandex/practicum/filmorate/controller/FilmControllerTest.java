package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.time.Month;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    static FilmController filmController;
    static Validator validator;

    @BeforeAll
    static void beforeAll() {
        filmController = new FilmController();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void addCorrectFilm() {
        Film film = new Film("name1", "description", LocalDate.now(), 100);
        Film createdFilm = filmController.addFilm(film);
        assertEquals(film.getId(), createdFilm.getId());
        assertEquals(film.getName(), createdFilm.getName());
    }

    @Test
    void addFilmWithBoundDate() {
        Film film = new Film("name2", "description", LocalDate.of(1895, Month.DECEMBER, 28), 100);
        Film createdFilm = filmController.addFilm(film);
        assertEquals(film.getId(), createdFilm.getId());
        assertEquals(film.getName(), createdFilm.getName());
    }

    @Test
    void addFilmWithOldDate() {
        Film film = new Film("name3", "description", LocalDate.of(1895, Month.DECEMBER, 27), 100);
        ValidationException ex = assertThrows(ValidationException.class, () -> filmController.addFilm(film));
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", ex.getMessage());
    }

    @Test
    void createFilmWithEmptyName() {
        Film film1 = new Film(null, "description", LocalDate.now(), 100);
        Film film2 = new Film("", "description", LocalDate.now(), 100);
        Film film3 = new Film(" ", "description", LocalDate.now(), 100);
        Set<ConstraintViolation<Film>> violations1 = validator.validate(film1);
        assertEquals(1, violations1.size());
        Set<ConstraintViolation<Film>> violations2 = validator.validate(film2);
        assertEquals(1, violations2.size());
        Set<ConstraintViolation<Film>> violations3 = validator.validate(film3);
        assertEquals(1, violations3.size());
    }

    @Test
    void createFilmWithLongDescription() {
        Film film1 = new Film("name1", "q".repeat(200), LocalDate.now(), 100);
        Film film2 = new Film("name2", "q".repeat(201), LocalDate.now(), 100);
        Set<ConstraintViolation<Film>> violations1 = validator.validate(film1);
        assertEquals(0, violations1.size());
        Set<ConstraintViolation<Film>> violations2 = validator.validate(film2);
        assertEquals(1, violations2.size());
    }

    @Test
    void createFilmWithNegativeDuration() {
        Film film1 = new Film("name1", "q", LocalDate.now(), 0);
        Film film2 = new Film("name2", "q", LocalDate.now(), -1);
        Set<ConstraintViolation<Film>> violations1 = validator.validate(film1);
        assertEquals(1, violations1.size());
        Set<ConstraintViolation<Film>> violations2 = validator.validate(film2);
        assertEquals(1, violations2.size());
    }
}