package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenreDao {
    Genre getGenre(int id);

    List<Genre> getGenres();

    Set<Genre> getFilmGenres(int filmId);
}
