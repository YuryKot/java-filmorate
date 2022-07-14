package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserStorageDaoImplTest {

    private final UserStorageDaoImpl userStorage;
    private final JdbcTemplate jdbcTemplate;

    @AfterEach
    void tearDown() {
        jdbcTemplate.update("DELETE FROM USERS");
    }

    @Test
    @DirtiesContext
    void addUser() {
        assertEquals(0, userStorage.getUsers().size());
        User user = new User("ivanivanov@mail.ru", "Иван777", "Иван", LocalDate.of(2000, 1,1));
        userStorage.addUser(user);
        User returnUser = userStorage.getUser(1);
        assertEquals(user.getName(), returnUser.getName());
    }

    @Test
    @DirtiesContext
    void updateUser() {
        assertEquals(0, userStorage.getUsers().size());
        User user = new User("ivanivanov@mail.ru", "Иван777", "Иван", LocalDate.of(2000, 1,1));
        userStorage.addUser(user);
        User updateUser = new User("ivanivanov@mail.ru", "Иван777", "newName", LocalDate.of(2000, 1,1));
        updateUser.setId(1);
        userStorage.updateUser(updateUser);
        User returnUser = userStorage.getUser(1);
        assertEquals(updateUser.getName(), returnUser.getName());
        updateUser.setId(2);
        assertThrows(NotFoundException.class, () -> userStorage.updateUser(updateUser));
    }

    @Test
    @DirtiesContext
    void getUsers() {
        assertEquals(0, userStorage.getUsers().size());
        User user1 = new User("ivanivanov@mail.ru", "Иван777", "Иван1", LocalDate.of(2000, 1,1));
        userStorage.addUser(user1);
        User user2 = new User("ivanivanov@mail.ru", "Иван777", "Иван2", LocalDate.of(2000, 1,1));
        userStorage.addUser(user2);
        assertEquals(2, userStorage.getUsers().size());
    }

    @Test
    @DirtiesContext
    void getUser() {
        assertEquals(0, userStorage.getUsers().size());
        User user1 = new User("ivanivanov@mail.ru", "Иван777", "Иван1", LocalDate.of(2000, 1,1));
        userStorage.addUser(user1);
        User user2 = new User("ivanivanov@mail.ru", "Иван777", "Иван2", LocalDate.of(2000, 1,1));
        userStorage.addUser(user2);
        User returnUser1 = userStorage.getUser(1);
        User returnUser2 = userStorage.getUser(2);
        assertEquals(user1.getName(), returnUser1.getName());
        assertEquals(user2.getName(), returnUser2.getName());
        assertThrows(NotFoundException.class, () -> userStorage.getUser(3));
    }

    @Test
    @DirtiesContext
    void isUserExists() {
        User user = new User("ivanivanov@mail.ru", "Иван777", "Иван", LocalDate.of(2000, 1,1));
        userStorage.addUser(user);
        assertTrue(userStorage.isUserExists(1));
        assertFalse(userStorage.isUserExists(2));
    }
}