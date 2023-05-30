package ru.practicum.shareit.user.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dao.UserDAO;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserDAO userDao;

    public UserDTO add(UserDTO userDto) {
        User user = UserMapper.toUser(userDto, userDto.getId());
        return UserMapper.toUserDto(userDao.add(user));
    }

    public UserDTO update(Long id, UserDTO userDto) {
        User user = UserMapper.toUser(userDto, id);
        return UserMapper.toUserDto(userDao.update(id, user));
    }

    public UserDTO findById(Long id) {
        User user = userDao.findById(id)
                .orElseThrow(() -> {
                            log.warn("Не найден пользователь с id: {}", id);
                            return new NotFoundException(String.format("не найден пользователь с id: %d", id));
                        }
                );
        return UserMapper.toUserDto(user);
    }

    public void delete(Long id) {
        userDao.delete(id);
    }

    public List<UserDTO> findAll() {
        return userDao.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

}
