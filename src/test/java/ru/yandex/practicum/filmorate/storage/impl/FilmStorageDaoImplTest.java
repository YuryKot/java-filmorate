package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmStorageDaoImplTest {

    private final FilmStorageDaoImpl filmStorageDao;
    private final JdbcTemplate jdbcTemplate;
    static Film film1;
    static Film film2;

    @BeforeAll
    static void beforeAll() {
        film1 = new Film("Name1", "Description", LocalDate.of(2000, 1, 1), 100);
        film1.setMpa(new Mpa(1, "G"));
        film2 = new Film("Name2", "Description", LocalDate.of(2000, 1, 1), 100);
        film2.setMpa(new Mpa(1, "G"));
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.update("DELETE FROM FILM_GENRE");
        jdbcTemplate.update("DELETE FROM FILM");
    }

    @Test
    @DirtiesContext
    void addFilm() {
        assertEquals(0, filmStorageDao.getFilms().size());
        filmStorageDao.addFilm(film1);
        assertEquals(1, filmStorageDao.getFilms().size());
        assertEquals(film1.getName(), filmStorageDao.getFilm(1).getName());
    }

    @Test
    @DirtiesContext
    void updateFilm() {
        assertEquals(0, filmStorageDao.getFilms().size());
        filmStorageDao.addFilm(film1);
        film2.setId(1);
        filmStorageDao.updateFilm(film2);
        Film getFilm = filmStorageDao.getFilm(1);
        assertEquals(film2.getName(), getFilm.getName());
    }

    @Test
    @DirtiesContext
    void getFilms() {
        assertEquals(0, filmStorageDao.getFilms().size());
        filmStorageDao.addFilm(film1);
        filmStorageDao.addFilm(film2);
        List<Film> films = filmStorageDao.getFilms();
        assertEquals(2, films.size());
        assertEquals(film1.getName(), films.get(0).getName());
        assertEquals(film2.getName(), films.get(1).getName());
    }

    @Test
    @DirtiesContext
    void getFilm() {
        filmStorageDao.addFilm(film1);
        assertEquals(film1.getName(), filmStorageDao.getFilm(1).getName());
        assertThrows(NotFoundException.class, () -> filmStorageDao.getFilm(2));
    }

    @Test
    @DirtiesContext
    void isFilmExists() {
        filmStorageDao.addFilm(film1);
        assertTrue(filmStorageDao.isFilmExists(1));
        assertFalse(filmStorageDao.isFilmExists(2));
    }
}