package ru.practicum.shareit.user;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.dto.UserDTO;

@UtilityClass
public class UserMapper {

    public static UserDTO toUserDto(User user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail());
    }

    public static User toUser(UserDTO userDto, Long id) {
        return new User(
                id,
                userDto.getName(),
                userDto.getEmail());
    }
}
