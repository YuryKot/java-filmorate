package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@Qualifier("UserStorageDaoImpl")
public class UserStorageDaoImpl implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserStorageDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        String sql = "INSERT INTO PUBLIC.USERS (NAME, LOGIN, BIRTHDAY, EMAIL) VALUES (?, ?, ?, ?)";
        User updateUser = user.checkUserName();
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(sql, new String[]{"USER_ID"});
            stmt.setString(1, updateUser.getName());
            stmt.setString(2, updateUser.getLogin());
            stmt.setDate(3, Date.valueOf(updateUser.getBirthday()));
            stmt.setString(4, updateUser.getEmail());
            return stmt;
        }, keyHolder);
        updateUser.setId(keyHolder.getKey().intValue());
        insertFriends(updateUser);
        log.info("Добавлен новый пользователь: {}", updateUser.toString());
        return updateUser;
    }

    @Override
    public User updateUser(User user) {
        if (isUserExists(user.getId())) {
            user = user.checkUserName();
            String sql = "UPDATE PUBLIC.USERS SET NAME = ?, LOGIN = ?, BIRTHDAY = ?, EMAIL = ? WHERE USER_ID = ?";
            jdbcTemplate.update(sql,
                                user.getName(),
                                user.getLogin(),
                                Date.valueOf(user.getBirthday()),
                                user.getEmail(),
                                user.getId());
            deleteFriends(user.getId());
            insertFriends(user);
            log.info("Обновлены параметры пользователя: {}", user.toString());
            return user;
        } else {
            throw new NotFoundException("Не найдено подходящего пользователя для обновления параметров");
        }
    }

    @Override
    public List<User> getUsers() {
        String getUsers = "SELECT * FROM PUBLIC.USERS";
        List<User> users = jdbcTemplate.query(getUsers, (rs, rowNum) -> makeUser(rs));
        for (User user : users) {
            setFriendsIdSet(user);
        }
        return users;
    }

    @Override
    public User getUser(int id) {
        String getUser = "SELECT * FROM PUBLIC.USERS WHERE USER_ID = ?";
        User user;
        try {
            user = jdbcTemplate.queryForObject(getUser, (rs, rowNum) -> makeUser(rs), id);
        } catch (DataAccessException ex) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден.", id));
        }
        setFriendsIdSet(user);
        return user;
    }

    @Override
    public boolean isUserExists(int id) {
        String sql = "SELECT * FROM PUBLIC.USERS WHERE USER_ID = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, id);
        return userRows.next();
    }

    private void setFriendsIdSet(User user) {
        String sql = "SELECT RECIPIENT_ID FROM PUBLIC.FRIENDS WHERE SENDER_ID = ?";
        Set<Integer> friendsIdSet = new HashSet<>(
                jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("recipient_id"), user.getId()));
        user.setFriendsIdSet(friendsIdSet);
    }

    private void insertFriends(User user) {
        if (user.getFriendsIdSet().isEmpty()) {
            return;
        }
        String sql = "INSERT INTO PUBLIC.FRIENDS VALUES (?, ?, ?)";
        for (int friendId : user.getFriendsIdSet()) {
            jdbcTemplate.update(sql, user.getId(), friendId, false);
        }
    }

    private void deleteFriends(int id) {
        String sql = "DELETE FROM PUBLIC.FRIENDS WHERE SENDER_ID = ?";
        jdbcTemplate.update(sql, id);
    }

    private User makeUser(ResultSet rs) throws SQLException {
        String name = rs.getString("name");
        String login = rs.getString("login");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        String email = rs.getString("email");
        User user = new User(email, login, name, birthday);
        user.setId(rs.getInt("user_id"));
        return user;
    }
}
