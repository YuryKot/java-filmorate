package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public interface UserService {

    User addUser(User user);

    User updateUser(User user);

    List<User> getUsers();

    User getUser(int id);

    boolean isUserExists(int id);

    void addFriend(int id, int friendId);

    void deleteFriend(int id, int friendId);

    Set<User> getCommonFriends(int id, int otherId);

    Set<User> getUserFriends(int id);
}
