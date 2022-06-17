package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    static Validator validator;
    static UserController userController;

    @BeforeAll
    static void beforeAll() {
        UserStorage userStorage = new InMemoryUserStorage();
        userController = new UserController(userStorage, new UserService(userStorage));
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void addCorrectUser() {
        User user = new User("email@mail.ru", "login", "name", LocalDate.now());
        User createdUser = userController.addUser(user);
        assertEquals(user.getId(), createdUser.getId());
    }

    @Test
    void addUserWithEmptyName() {
        User user1 = new User("email@mail.ru", "login1", null, LocalDate.now());
        User user2 = new User("email@mail.ru", "login2", "", LocalDate.now());
        User user3 = new User("email@mail.ru", "login3", " ", LocalDate.now());
        User createdUser1 = userController.addUser(user1);
        User createdUser2 = userController.addUser(user2);
        User createdUser3 = userController.addUser(user3);
        assertEquals(user1.getLogin(), createdUser1.getName());
        assertEquals(user2.getLogin(), createdUser2.getName());
        assertEquals(user3.getLogin(), createdUser3.getName());
    }

    @Test
    void createUserWithInvalidEmail() {
        User user1 = new User("email@mail.ru@", "login1", "name", LocalDate.now());
        User user2 = new User("mail.ru", "login1", "name", LocalDate.now());
        User user3 = new User("email", "login1", "name", LocalDate.now());
        Set<ConstraintViolation<User>> violations1 = validator.validate(user1);
        assertEquals(1, violations1.size());
        Set<ConstraintViolation<User>> violations2 = validator.validate(user2);
        assertEquals(1, violations2.size());
        Set<ConstraintViolation<User>> violations3 = validator.validate(user3);
        assertEquals(1, violations3.size());
    }

    @Test
    void createUserWithEmptyLogin() {
        User user1 = new User("email@mail.ru", null, "name", LocalDate.now());
        User user2 = new User("email@mail.ru", "", "name", LocalDate.now());
        Set<ConstraintViolation<User>> violations1 = validator.validate(user1);
        assertEquals(1, violations1.size());
        Set<ConstraintViolation<User>> violations2 = validator.validate(user2);
        assertEquals(1, violations2.size());
    }

    @Test
    void createUserWithSpaceInLogin() {
        User user1 = new User("email@mail.ru", " login", "name", LocalDate.now());
        User user2 = new User("email@mail.ru", "log in", "name", LocalDate.now());
        User user3 = new User("email@mail.ru", "login ", "name", LocalDate.now());
        Set<ConstraintViolation<User>> violations1 = validator.validate(user1);
        assertEquals(1, violations1.size());
        Set<ConstraintViolation<User>> violations2 = validator.validate(user2);
        assertEquals(1, violations2.size());
        Set<ConstraintViolation<User>> violations3 = validator.validate(user3);
        assertEquals(1, violations3.size());
    }

    @Test
    void createUserWithBirthdayInFuture() {
        User user = new User("email@mail.ru", "login", "name", LocalDate.of(2222, 01, 01));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

}