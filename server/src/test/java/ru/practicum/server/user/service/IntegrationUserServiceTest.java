package ru.practicum.server.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.user.User;
import ru.practicum.server.user.dto.UserDTO;
import ru.practicum.server.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(properties = {
        "spring.config.name=application-test",
        "spring.config.location=classpath:application-test.properties"
}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class IntegrationUserServiceTest {

    private final UserService userService;
    private final UserRepository userRepository;

    private UserDTO userDto = UserDTO.builder()
            .name("name")
            .email("user@email.com")
            .build();

    @Test
    void createUserTest() {
        UserDTO user = userService.createUser(userDto);

        assertNotNull(user.getId());
        Assertions.assertEquals(userDto.getEmail(), user.getEmail());
        Assertions.assertEquals(userDto.getName(), user.getName());

        User createdUser = userRepository.findById(user.getId()).orElse(null);
        assertNotNull(createdUser);
        Assertions.assertEquals(userDto.getEmail(), createdUser.getEmail());
        Assertions.assertEquals(userDto.getName(), createdUser.getName());
    }

    @Test
    void createUserDuplicateEmailTest() {
        UserDTO userDto = new UserDTO();
        userDto.setEmail("test@test.com");
        userDto.setName("Test tes");

        UserDTO userDtoDuplicate = new UserDTO();
        userDtoDuplicate.setEmail("test@test.com");
        userDtoDuplicate.setName("Test tes");

        userService.createUser(userDto);
        assertThrows(DataIntegrityViolationException.class,
                () -> {
                    userService.createUser(userDtoDuplicate);
                });
    }

    @Test
    @Sql("/data/test-users.sql")
    void findAllUsersTest() {
        List<UserDTO> userList = userService.findAllUsers();

        assertEquals(3, userList.size());

        assertEquals("User 1", userList.get(0).getName());
        assertEquals("User 2", userList.get(1).getName());
        assertEquals("User 3", userList.get(2).getName());
    }

    @Test
    void findUserByIdTest() {
        UserDTO user = userService.createUser(userDto);

        Long userId = user.getId();

        UserDTO inputUser = userService.findUserById(userId);

        assertNotNull(inputUser);
        Assertions.assertEquals(user.getEmail(), inputUser.getEmail());
        Assertions.assertEquals(user.getName(), inputUser.getName());
    }

    @Test
    void updateUserTest() {
        UserDTO user = userService.createUser(userDto);

        Long userId = user.getId();

        UserDTO fieldsToUpdate = new UserDTO();
        fieldsToUpdate.setEmail("updated@example.com");
        fieldsToUpdate.setName("Updated User");

        UserDTO updatedUserDto = userService.updateUser(userId, fieldsToUpdate);

        assertNotNull(updatedUserDto);
        Assertions.assertEquals("Updated User", updatedUserDto.getName());
        Assertions.assertEquals("updated@example.com", updatedUserDto.getEmail());
    }

    @Test
    void deleteUserByIdTest() {
        UserDTO user = userService.createUser(userDto);

        Long userId = user.getId();

        userService.deleteUserById(userId);

        Assertions.assertFalse(userRepository.existsById(userId));
    }
}
