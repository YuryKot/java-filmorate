package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreDao;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@Qualifier("FilmStorageDaoImpl")
public class FilmStorageDaoImpl implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreDao genreDao;
    private final MpaStorage mpaStorage;

    public FilmStorageDaoImpl(JdbcTemplate jdbcTemplate, GenreDao genreDao, MpaStorage mpaStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDao = genreDao;
        this.mpaStorage = mpaStorage;
    }

    @Override
    public Film addFilm(Film film) {
        film.validateFilm();
        String sql = "INSERT INTO PUBLIC.FILM (NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
                            PreparedStatement stmt = con.prepareStatement(sql, new String[]{"FILM_ID"});
                            stmt.setString(1, film.getName());
                            stmt.setString(2, film.getDescription());
                            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
                            stmt.setInt(4, film.getDuration());
                            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());
        int mpaId = film.getMpa().getId();
        film.setMpa(mpaStorage.getMpaById(mpaId));
        insertGenres(film);
        insertLikes(film);
        log.info("Добавлен новый фильм: {}", film.toString());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (isFilmExists(film.getId())) {
            film.validateFilm();
            String sql = "UPDATE PUBLIC.FILM SET " +
                    "NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA = ?" +
                    "WHERE FILM_ID = ?";
            jdbcTemplate.update(sql,
                                film.getName(),
                                film.getDescription(),
                                film.getReleaseDate(),
                                film.getDuration(),
                                film.getMpa().getId(),
                                film.getId());
            int mpaId = film.getMpa().getId();
            film.setMpa(mpaStorage.getMpaById(mpaId));
            deleteGenres(film.getId());
            insertGenres(film);
            deleteLikes(film.getId());
            insertLikes(film);
            log.info("Параметры фильма обновлены: {}", film.toString());
            return film;
        } else {
            throw new NotFoundException("Не найдено подходящего фильма для обновления параметров - id = " + film.getId());
        }
    }

    @Override
    public List<Film> getFilms() {
        String sql = "SELECT * FROM PUBLIC.FILM";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
        for (Film film : films) {
            film.setGenres(genreDao.getFilmGenres(film.getId()));
            setUsersIdLikesSet(film);
        }
        return films;
    }

    @Override
    public Film getFilm(int id) {
        String getFilm = "SELECT * FROM PUBLIC.FILM WHERE FILM_ID = ?";
        Film film;
        try {
            film = jdbcTemplate.queryForObject(getFilm, (rs, rowNum) -> makeFilm(rs), id);
        } catch (DataAccessException ex) {
            throw new NotFoundException(String.format("Фильм с id = %d не найден", id));
        }
        film.setGenres(genreDao.getFilmGenres(id));
        setUsersIdLikesSet(film);
        return film;
    }

    public boolean isFilmExists(int id) {
        String sql = "SELECT * FROM PUBLIC.FILM WHERE FILM_ID = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql, id);
        return filmRows.next();
    }

    private void insertLikes(Film film) {
        if (film.getUsersIdLikesSet().isEmpty()) {
            return;
        }
        String sql = "INSERT INTO PUBLIC.FILM_USER VALUES (?, ?)";
        for (Integer userId : film.getUsersIdLikesSet()) {
            jdbcTemplate.update(sql, film.getId(), userId);
        }
    }

    private void deleteLikes(int id) {
        String sql = "DELETE FROM PUBLIC.FILM_USER WHERE FILM_ID = ?";
        jdbcTemplate.update(sql, id);
    }

    private void deleteGenres(int id) {
        String sql = "DELETE FROM PUBLIC.FILM_GENRE WHERE FILM_ID = ?";
        jdbcTemplate.update(sql, id);
    }

    private void insertGenres(Film film) {
        if (film.getGenres().isEmpty()) {
            return;
        }
        String sql = "INSERT INTO PUBLIC.FILM_GENRE VALUES (?, ?)";
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(sql, film.getId(), genre.getId());
        }
        film.setGenres(genreDao.getFilmGenres(film.getId()));
    }

    private void setUsersIdLikesSet(Film film) {
        String sql = "SELECT USER_ID FROM PUBLIC.FILM_USER WHERE FILM_ID = ?";
        Set<Integer> usersIdLikesSet = new HashSet<>(
                jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("user_id"), film.getId()));
        film.setUsersIdLikesSet(usersIdLikesSet);
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        int duration = rs.getInt("duration");
        int mpaId = rs.getInt("mpa");
        Film film = new Film(name, description, releaseDate, duration);
        film.setId(rs.getInt("film_id"));
        film.setMpa(mpaStorage.getMpaById(mpaId));
        return film;
    }
}
