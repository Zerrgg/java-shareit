package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDTO;

import java.util.List;

public interface UserService {

    UserDTO add(UserDTO userDto);

    UserDTO update(Long id, UserDTO userDto);

    UserDTO findById(Long id);

    void delete(Long id);

    List<UserDTO> findAll();

}
