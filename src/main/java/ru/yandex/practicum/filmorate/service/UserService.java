package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(int id, int friendId) {
        User user = userStorage.getUser(id);
        userStorage.getUser(friendId);
        Set<Integer> friendsIdList = user.getFriendsIdSet();
        friendsIdList.add(friendId);
        user.setFriendsIdSet(friendsIdList);
        userStorage.updateUser(user);
    }

    public void deleteFriend(int id, int friendId) {
        User user = userStorage.getUser(id);
        userStorage.getUser(friendId);
        Set<Integer> friendsIdList = user.getFriendsIdSet();
        if (!friendsIdList.contains(friendId)) {
            throw new ValidationException(String.format("Пользователи с id %d, %d не являются друзьями", id, friendId));
        }
        friendsIdList.remove(friendId);
        user.setFriendsIdSet(friendsIdList);
        userStorage.updateUser(user);
    }

    public Set<User> getCommonFriends(int id, int otherId) {
        User user = userStorage.getUser(id);
        User friend = userStorage.getUser(otherId);
        Set<Integer> firstFriendsList = user.getFriendsIdSet();
        Set<Integer> secondFriendsList = friend.getFriendsIdSet();
        Set<Integer> commonFriends = firstFriendsList.stream().filter(secondFriendsList::contains).collect(toSet());
        return getUserSetFromIdUserSet(commonFriends);
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
