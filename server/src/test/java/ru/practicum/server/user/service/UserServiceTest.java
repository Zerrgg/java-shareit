package ru.practicum.server.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.server.user.User;
import ru.practicum.server.user.UserMapper;
import ru.practicum.server.user.dto.UserDTO;
import ru.practicum.server.user.repository.UserRepository;
import ru.practicum.server.user.service.impl.UserServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceTest {

    public static final long ID = 1L;

    private UserService userService;
    private UserRepository userRepository;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    public void init() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);

        user = User.builder()
                .id(ID)
                .name("user")
                .email("user@email.com")
                .build();
    }

    @Test
    void createUserTest() {
        userDTO = UserMapper.toUserDTO(user);

        when(userRepository.save(any()))
                .thenReturn(user);

        UserDTO savedDto = userService.createUser(userDTO);

        assertNotNull(savedDto);
        assertEquals(1, savedDto.getId());
        assertEquals(user.getName(), savedDto.getName());
        assertEquals(user.getEmail(), savedDto.getEmail());

        verify(userRepository, times(1))
                .save(any());
    }

    @Test
    void updateUserTest() {
        user.setName("updated name");

        userDTO = UserMapper.toUserDTO(user);

        when(userRepository.save(any()))
                .thenReturn(user);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        UserDTO savedDto = userService.updateUser(ID, userDTO);

        assertNotNull(savedDto);
        assertEquals(1, savedDto.getId());
        assertEquals(userDTO.getName(), savedDto.getName());
        assertEquals(userDTO.getEmail(), savedDto.getEmail());

        verify(userRepository, times(1))
                .save(any());
    }

    @Test
    void findUserByIdTest() {
        userDTO = UserMapper.toUserDTO(user);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        UserDTO savedDto = userService.findUserById(ID);

        assertNotNull(savedDto);
        assertEquals(1, savedDto.getId());
        assertEquals(userDTO.getName(), savedDto.getName());
        assertEquals(userDTO.getEmail(), savedDto.getEmail());

        verify(userRepository, times(1))
                .findById(anyLong());
    }

    @Test
    void deleteUserByIdTest() {
        userService.deleteUserById(ID);

        verify(userRepository, times(1))
                .deleteById(ID);
    }

    @Test
    void findAllUsersTest() {

        when(userRepository.findAll())
                .thenReturn(Collections.singletonList(user));

        List<UserDTO> dtoList = userService.findAllUsers();

        assertNotNull(dtoList);
        assertEquals(1, dtoList.size());
        assertEquals(user.getId(), dtoList.get(0).getId());

        verify(userRepository, times(1))
                .findAll();
    }
}