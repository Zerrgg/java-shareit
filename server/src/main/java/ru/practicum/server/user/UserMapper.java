package ru.practicum.server.user;

import lombok.experimental.UtilityClass;
import ru.practicum.server.user.dto.UserDTO;

@UtilityClass
public class UserMapper {

    public static UserDTO toUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User toUser(UserDTO userDTO) {
        return User.builder()
                .id(userDTO.getId())
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .build();
    }
}
