package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserDao {
    User add(User user);

    User update(Long id, User user);

    User findById(Long id);

    void delete(Long id);

    List<User> findAll();
}
