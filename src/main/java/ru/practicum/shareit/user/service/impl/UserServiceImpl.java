package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.UserMapper.toUser;
import static ru.practicum.shareit.user.UserMapper.toUserDTO;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        User user = toUser(userDTO);
        user = userRepository.save(user);
        return toUserDTO(user);
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        User user = patchUser(userDTO, userId);
        user.setId(userId);
        user = userRepository.save(user);
        return toUserDTO(user);
    }

    @Override
    public UserDTO findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                            log.warn("Не найден пользователь с id: {}", id);
                            return new NotFoundException(String.format("не найден пользователь с id: %d", id));
                        }
                );
        return toUserDTO(user);
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<UserDTO> findAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDTO)
                .collect(Collectors.toList());
    }

    private User patchUser(UserDTO patch, Long userId) {
        UserDTO entry = findUserById(userId);
        String name = patch.getName();
        if (StringUtils.hasText(name)) {
            entry.setName(name);
        }

        String oldEmail = entry.getEmail();
        String newEmail = patch.getEmail();
        if (StringUtils.hasText(newEmail) && !oldEmail.equals(newEmail)) {
            entry.setEmail(newEmail);
        }
        return toUser(entry);
    }

}
