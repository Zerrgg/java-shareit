package ru.practicum.shareit.user.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserDao userDao;

    public User add(User user) {
        return userDao.add(user);
    }

    public User update(Long id, User user) {
        return userDao.update(id, user);
    }

    public User findById(Long id) {
        return userDao.findById(id);
    }

    public void delete(Long id) {
        userDao.delete(id);
    }

    public List<User> findAll() {
        return userDao.findAll();
    }

}
