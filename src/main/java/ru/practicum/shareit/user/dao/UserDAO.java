package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface UserDAO {
    User add(User user);

    User update(Long id, User user);

    Optional<User> findById(Long id);

    void delete(Long id);

    List<User> findAll();
}
