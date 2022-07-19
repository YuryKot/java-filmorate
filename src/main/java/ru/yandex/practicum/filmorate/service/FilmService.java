package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getFilms();

    Film getFilm(int id);

    void addLike(int id, int userId);

    void deleteLike(int id, int userId);

    List<Film> getPopularFilms(int count);
}
