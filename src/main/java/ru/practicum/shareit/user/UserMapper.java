package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserMapper {

    public UserDto intoDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public User intoModel(UserDto userDto, Long id) {
        return new User(id, userDto.getName(), userDto.getEmail());
    }

    public List<UserDto> intoDtoList(List<User> users) {
        List<UserDto> result = new ArrayList<>();
        for (User user : users) {
            result.add(intoDto(user));
        }
        return result;
    }
}
