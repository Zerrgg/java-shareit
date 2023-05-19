package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.markers.Create;
import ru.practicum.shareit.exception.markers.Update;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.dto.UserDto;


import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    public static final int MIN_ID_VALUE = 1;
    public static final String NULL_USER_ID_MESSAGE = "userID is null";

    private final UserMapper userMapper;
    private final UserService userService;

    @PostMapping
    public UserDto add(@Validated({Create.class})
                       @RequestBody UserDto userDto) {
        User user = userMapper.intoModel(userDto, userDto.getId());
        return userMapper.intoDto(userService.add(user));
    }

    @GetMapping("/{userId}")
    public UserDto findById(@NotNull(message = (NULL_USER_ID_MESSAGE))
                            @Min(MIN_ID_VALUE)
                            @PathVariable Long userId) {
        return userMapper.intoDto(userService.findById(userId));
    }

    @GetMapping
    public List<UserDto> findAll() {
        return userMapper.intoDtoList(userService.findAll());
    }

    @PatchMapping("/{userId}")
    public UserDto update(@NotNull(message = (NULL_USER_ID_MESSAGE))
                          @Min(MIN_ID_VALUE)
                          @PathVariable Long userId,
                          @Validated({Update.class})
                          @RequestBody UserDto userDto) {
        User user = userMapper.intoModel(userDto, userId);
        return userMapper.intoDto(userService.update(userId, user));
    }

    @DeleteMapping("/{userId}")
    public void delete(@NotNull(message = (NULL_USER_ID_MESSAGE))
                       @Min(MIN_ID_VALUE)
                       @PathVariable Long userId) {
        userService.delete(userId);
    }
}
