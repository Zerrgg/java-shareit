package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserMapperTest {

    @Test
    void toUserDTOTest() {
        User user = new User(1L, "user1", "user1@email.com");
        UserDTO dto = UserMapper.toUserDTO(user);

        assertNotNull(dto);
        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getName(), dto.getName());
        assertEquals(user.getEmail(), dto.getEmail());
    }

    @Test
    void toUserTest() {
        UserDTO dto = new UserDTO(1L, "user1", "user1@email.com");
        User fromDtoUser = UserMapper.toUser(dto);

        assertNotNull(fromDtoUser);
        assertEquals(dto.getId(), fromDtoUser.getId());
        assertEquals(dto.getName(), fromDtoUser.getName());
        assertEquals(dto.getEmail(), fromDtoUser.getEmail());
    }
}