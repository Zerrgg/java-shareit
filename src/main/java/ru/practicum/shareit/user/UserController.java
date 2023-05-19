package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.markers.Create;
import ru.practicum.shareit.exception.markers.Update;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    public static final String NULL_USER_ID_MESSAGE = "userID is null";

    private final UserService userService;

    @PostMapping
    public UserDto add(@Validated({Create.class})
                       @RequestBody UserDto userDto) {
        return userService.add(userDto);
    }

    @GetMapping("/{userId}")
    public UserDto findById(@NotNull(message = (NULL_USER_ID_MESSAGE))
                            @Positive
                            @PathVariable Long userId) {
        return userService.findById(userId);
    }

    @GetMapping
    public List<UserDto> findAll() {
        return userService.findAll();
    }

    @PatchMapping("/{userId}")
    public UserDto update(@NotNull(message = (NULL_USER_ID_MESSAGE))
                          @Positive
                          @PathVariable Long userId,
                          @Validated({Update.class})
                          @RequestBody UserDto userDto) {
        return userService.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@NotNull(message = (NULL_USER_ID_MESSAGE))
                       @Positive
                       @PathVariable Long userId) {
        userService.delete(userId);
    }
}
