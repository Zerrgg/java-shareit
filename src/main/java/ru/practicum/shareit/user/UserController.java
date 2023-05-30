package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.markers.Create;
import ru.practicum.shareit.exception.markers.Update;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.dto.UserDTO;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    public static final String NULL_USER_ID_MESSAGE = "userID is null";

    private final UserService userService;

    @PostMapping
    public UserDTO add(@Validated({Create.class})
                       @RequestBody UserDTO userDto) {
        log.info("POST Запрос на добавление пользователя {}", userDto);
        return userService.add(userDto);
    }

    @GetMapping("/{userId}")
    public UserDTO findById(@NotNull(message = (NULL_USER_ID_MESSAGE))
                            @Positive
                            @PathVariable Long userId) {
        log.info("GET Запрос на получение пользователя по id-{}", userId);
        return userService.findById(userId);
    }

    @GetMapping
    public List<UserDTO> findAll() {
        log.info("GET Запрос на получение всех пользователей");
        return userService.findAll();
    }

    @PatchMapping("/{userId}")
    public UserDTO update(@NotNull(message = (NULL_USER_ID_MESSAGE))
                          @Positive
                          @PathVariable Long userId,
                          @Validated({Update.class})
                          @RequestBody UserDTO userDto) {
        log.info("PATCH Запрос на обновление пользователя по id-{}", userId);
        return userService.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@NotNull(message = (NULL_USER_ID_MESSAGE))
                       @Positive
                       @PathVariable Long userId) {
        log.info("DELETE Запрос на удаление пользователя по id-{}", userId);
        userService.delete(userId);
    }
}
