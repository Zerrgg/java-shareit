package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserService {

    public User add(User user);

    public User update(Long id, User user);

    public User findById(Long id);

    public void delete(Long id);

    public List<User> findAll();

}
