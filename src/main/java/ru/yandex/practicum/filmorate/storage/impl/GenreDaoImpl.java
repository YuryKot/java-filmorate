package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class GenreDaoImpl implements GenreDao {

    private final JdbcTemplate jdbcTemplate;

    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Genre getGenre(int id) {
        String sql = "SELECT * FROM PUBLIC.GENRE WHERE GENRE_ID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeGenre(rs), id);
        } catch (DataAccessException ex) {
            throw new NotFoundException(String.format("Жанр с id = %d не найден.", id));
        }
    }

    public List<Genre> getGenres() {
        String sql = "SELECT * FROM PUBLIC.GENRE";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
    }

    @Override
    public Set<Genre> getFilmGenres(int filmId) {
        String sql = "SELECT G.GENRE_ID, G.NAME FROM PUBLIC.GENRE G " +
                "JOIN PUBLIC.FILM_GENRE FG ON G.GENRE_ID = FG.GENRE_ID " +
                "WHERE FILM_ID = ? ORDER BY G.GENRE_ID";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), filmId));
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        int id = rs.getInt("genre_id");
        String name = rs.getString("name");
        return new Genre(id, name);
    }
}
