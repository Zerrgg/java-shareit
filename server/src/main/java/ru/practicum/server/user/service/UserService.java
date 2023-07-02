package ru.practicum.server.user.service;

import ru.practicum.server.user.dto.UserDTO;

import java.util.List;

public interface UserService {

    UserDTO createUser(UserDTO userDTO);

    UserDTO updateUser(Long id, UserDTO userDTO);

    UserDTO findUserById(Long id);

    void deleteUserById(Long id);

    List<UserDTO> findAllUsers();

}
