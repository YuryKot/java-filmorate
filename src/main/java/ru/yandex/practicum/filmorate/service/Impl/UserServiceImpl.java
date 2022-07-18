package ru.yandex.practicum.filmorate.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(@Qualifier("UserStorageDaoImpl") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUser(int id) {
        return userStorage.getUser(id);
    }

    public boolean isUserExists(int id) {
        return userStorage.isUserExists(id);
    }

    public void addFriend(int id, int friendId) {
        User user = userStorage.getUser(id);
        if (!userStorage.isUserExists(friendId)) {
            throw new NotFoundException(
                    String.format("Пользователь с id = %d для добавления в друзья не найден.", friendId));
        }
        Set<Integer> friendsIdList = user.getFriendsIdSet();
        friendsIdList.add(friendId);
        user.setFriendsIdSet(friendsIdList);
        userStorage.updateUser(user);
    }

    public void deleteFriend(int id, int friendId) {
        User user = userStorage.getUser(id);
        if (!userStorage.isUserExists(friendId)) {
            throw new NotFoundException(
                    String.format("Пользователь с id = %d для удаления из друзей не найден.", friendId));
        }
        Set<Integer> friendsIdList = user.getFriendsIdSet();
        if (!friendsIdList.contains(friendId)) {
            throw new ValidationException(String.format("Пользователи с id %d, %d не являются друзьями", id, friendId));
        }
        friendsIdList.remove(friendId);
        user.setFriendsIdSet(friendsIdList);
        userStorage.updateUser(user);
    }

    public Set<User> getCommonFriends(int id, int otherId) {
        return userStorage.getCommonFriends(id, otherId);
    }

    public Set<User> getUserFriends(int id) {
        User user = userStorage.getUser(id);
        Set<Integer> friendsIdList = user.getFriendsIdSet();
        return getUserSetFromIdUserSet(friendsIdList);
    }

    private Set<User> getUserSetFromIdUserSet(Set<Integer> idUserSet) {
        Set<User> userSet = new HashSet<>();
        for (int idUser : idUserSet) {
            userSet.add(userStorage.getUser(idUser));
        }
        return userSet;
    }
}
