package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserStorage {
    User addUser(User user);

    User updateUser(User user);

    List<User> getUsers();

    User getUser(int id);

    boolean isUserExists(int id);

    default Set<User> getCommonFriends(int id, int otherId) {
        throw new UnsupportedOperationException();
    }

}
