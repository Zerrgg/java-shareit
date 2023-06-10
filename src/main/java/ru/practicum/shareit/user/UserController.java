package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.markers.Create;
import ru.practicum.shareit.exception.markers.Update;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.dto.UserDTO;

import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDTO createUser(@Validated({Create.class})
                              @RequestBody UserDTO userDto) {
        log.info("POST Запрос на добавление пользователя {}", userDto);
        return userService.createUser(userDto);
    }

    @GetMapping("/{userId}")
    public UserDTO findById(@Positive
                            @PathVariable Long userId) {
        log.info("GET Запрос на получение пользователя по id-{}", userId);
        return userService.findUserById(userId);
    }

    @GetMapping
    public List<UserDTO> findAll() {
        log.info("GET Запрос на получение всех пользователей");
        return userService.findAllUsers();
    }

    @PatchMapping("/{userId}")
    public UserDTO update(@Positive
                          @PathVariable Long userId,
                          @Validated({Update.class})
                          @RequestBody UserDTO userDto) {
        log.info("PATCH Запрос на обновление пользователя по id-{}", userId);
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@Positive
                       @PathVariable Long userId) {
        log.info("DELETE Запрос на удаление пользователя по id-{}", userId);
        userService.deleteUserById(userId);
    }
}
