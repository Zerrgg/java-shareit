package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserDTO createUser(UserDTO userDTO) {
        User user = UserMapper.toUser(userDTO);
        return UserMapper.toUserDTO(userRepository.save(user));
    }

    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        User user = patchUser(userDTO, userId);
        return UserMapper.toUserDTO(userRepository.save(user));
    }

    public UserDTO findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                            log.warn("Не найден пользователь с id: {}", id);
                            return new NotFoundException(String.format("не найден пользователь с id: %d", id));
                        }
                );
        return UserMapper.toUserDTO(user);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    public List<UserDTO> findAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDTO)
                .collect(Collectors.toList());
    }

    private User patchUser(UserDTO patch, Long userId) {
        UserDTO entry = findUserById(userId);
        String name = patch.getName();
        if (name != null && !name.isBlank()) {
            entry.setName(name);
        }

        String oldEmail = entry.getEmail();
        String newEmail = patch.getEmail();
        if (newEmail != null && !newEmail.isBlank() && !oldEmail.equals(newEmail)) {
            entry.setEmail(newEmail);
        }
        return UserMapper.toUser(entry);
    }

}
