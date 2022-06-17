package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(int id, int userId) {
        Film film = filmStorage.getFilm(id);
        userStorage.getUser(userId);
        Set<Integer> usersIdLikesSet = film.getUsersIdLikesSet();
        usersIdLikesSet.add(userId);
        film.setUsersIdLikesSet(usersIdLikesSet);
        filmStorage.updateFilm(film);
    }

    public void deleteLike(int id, int userId) {
        Film film = filmStorage.getFilm(id);
        userStorage.getUser(userId);
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
