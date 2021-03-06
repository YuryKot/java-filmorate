package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.Impl.FilmServiceImpl;
import ru.yandex.practicum.filmorate.service.Impl.UserServiceImpl;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryMpaStorage;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryUserStorage;

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
    static Mpa mpa = InMemoryMpaStorage.MPA.get(1);

    @BeforeAll
    static void beforeAll() {
        FilmStorage filmStorage = new InMemoryFilmStorage();
        UserService userService = new UserServiceImpl(new InMemoryUserStorage());
        filmController = new FilmController(new FilmServiceImpl(filmStorage, userService));
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
        assertEquals("???????? ???????????? ???? ?????????? ???????? ???????????? 28 ?????????????? 1895 ????????", ex.getMessage());
    }

    @Test
    void createFilmWithEmptyName() {
        Film film1 = new Film(null, "description", LocalDate.now(), 100);
        film1.setMpa(mpa);
        Film film2 = new Film("", "description", LocalDate.now(), 100);
        film2.setMpa(mpa);
        Film film3 = new Film(" ", "description", LocalDate.now(), 100);
        film3.setMpa(mpa);
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
        film1.setMpa(mpa);
        Film film2 = new Film("name2", "q".repeat(201), LocalDate.now(), 100);
        film2.setMpa(mpa);
        Set<ConstraintViolation<Film>> violations1 = validator.validate(film1);
        assertEquals(0, violations1.size());
        Set<ConstraintViolation<Film>> violations2 = validator.validate(film2);
        assertEquals(1, violations2.size());
    }

    @Test
    void createFilmWithNegativeDuration() {
        Film film1 = new Film("name1", "q", LocalDate.now(), 0);
        film1.setMpa(mpa);
        Film film2 = new Film("name2", "q", LocalDate.now(), -1);
        film2.setMpa(mpa);
        Set<ConstraintViolation<Film>> violations1 = validator.validate(film1);
        assertEquals(1, violations1.size());
        Set<ConstraintViolation<Film>> violations2 = validator.validate(film2);
        assertEquals(1, violations2.size());
    }
}