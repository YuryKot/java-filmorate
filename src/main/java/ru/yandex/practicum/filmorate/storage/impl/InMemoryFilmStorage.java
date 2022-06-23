package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private static int idCounter = 1;

    @Override
    public Film addFilm(Film film) {
        film.validateFilm();
        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм: {}", film.toString());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Не найдено подходящего фильма для обновления параметров - id = " + film.getId());
        }
        film.validateFilm();
        films.put(film.getId(), film);
        log.info("Параметры фильма обновлены: {}", film.toString());
        return film;
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilm(int id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException(String.format("Фильм с id = %d не найден", id));
        }
        return films.get(id);
    }

    private int generateId() {
        return idCounter++;
    }
}
