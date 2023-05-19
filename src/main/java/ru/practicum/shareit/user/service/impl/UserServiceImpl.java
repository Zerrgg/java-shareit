package ru.practicum.shareit.user.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserDao userDao;
    private UserMapper userMapper;

    public UserDto add(UserDto userDto) {
        User user = userMapper.toUser(userDto, userDto.getId());
        return userMapper.toUserDto(userDao.add(user));
    }

    public UserDto update(Long id, UserDto userDto) {
        User user = userMapper.toUser(userDto, id);
        return userMapper.toUserDto(userDao.update(id, user));
    }

    public UserDto findById(Long id) {
        return userMapper.toUserDto(userDao.findById(id)
                .orElseThrow(() -> new UserNotFoundException("не найден пользователь с id: " + id)));
    }

    public void delete(Long id) {
        userDao.delete(id);
    }

    public List<UserDto> findAll() {
        return userDao.findAll()
                .stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

}
