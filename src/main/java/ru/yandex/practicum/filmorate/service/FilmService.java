package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilm(int id) {
        return filmStorage.getFilm(id);
    }

    public void addLike(int id, int userId) {
        Film film = filmStorage.getFilm(id);
        if (!userService.isUserExists(userId)) {
            throw new NotFoundException(String.format("Пользователь с userId = %d не найден.", userId));
        }
        Set<Integer> usersIdLikesSet = film.getUsersIdLikesSet();
        usersIdLikesSet.add(userId);
        film.setUsersIdLikesSet(usersIdLikesSet);
        filmStorage.updateFilm(film);
    }

    public void deleteLike(int id, int userId) {
        Film film = filmStorage.getFilm(id);
        if (!userService.isUserExists(userId)) {
            throw new NotFoundException(String.format("Пользователь с userId = %d не найден.", userId));
        }
        Set<Integer> usersIdLikesSet = film.getUsersIdLikesSet();
        if (!usersIdLikesSet.contains(userId)) {
            throw new ValidationException(
                    String.format("Фильму с id = %d пользователь с userId = %d лайк не ставил", id, userId));
        }
        usersIdLikesSet.remove(userId);
        film.setUsersIdLikesSet(usersIdLikesSet);
        filmStorage.updateFilm(film);
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> films = filmStorage.getFilms();
        return films.stream()
                .sorted(Comparator.comparing(x -> (-x.getUsersIdLikesSet().size())))
                .limit(count)
                .collect(Collectors.toList());
    }
}
