package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private static int idCounter = 1;

    @Override
    public User addUser(User user) {
        user = user.checkUserName();
        user.setId(generateId());
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь: {}", user.toString());
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Не найдено подходящего пользователя для обновления параметров");
        }
        user = user.checkUserName();
        users.put(user.getId(), user);
        log.info("Обновлены параметры пользователя: {}", user.toString());
        return user;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(int id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден.", id));
        }
        return users.get(id);
    }

    private int generateId() {
        return idCounter++;
    }

    public boolean isUserExists(int id) {
        return users.containsKey(id);
    }
}
