package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDao;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDaoImplTest {

    private final GenreDao genreDao;
    private final JdbcTemplate jdbcTemplate;

    @Test
    void getGenre() {
        assertEquals("Комедия", genreDao.getGenre(1).getName());
        assertEquals("Драма", genreDao.getGenre(2).getName());
        assertThrows(NotFoundException.class, () -> genreDao.getGenre(10));
    }

    @Test
    void getGenres() {
        List<Genre> genres = genreDao.getGenres();
        assertEquals(6, genres.size());
    }
}