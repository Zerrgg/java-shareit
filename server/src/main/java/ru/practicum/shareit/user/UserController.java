package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDTO createUser(@RequestBody UserDTO userDto, BindingResult result) {
        log.info("POST Запрос на добавление пользователя {}", userDto);
        return userService.createUser(userDto);
    }

    @GetMapping("/{userId}")
    public UserDTO findUserById(@PathVariable Long userId) {
        log.info("GET Запрос на получение пользователя по id-{}", userId);
        return userService.findUserById(userId);
    }

    @GetMapping
    public List<UserDTO> findAllUsers() {
        log.info("GET Запрос на получение всех пользователей");
        return userService.findAllUsers();
    }

    @PatchMapping("/{userId}")
    public UserDTO updateUser(@PathVariable Long userId,
                              @RequestBody UserDTO userDto,
                              BindingResult result) {
        log.info("PATCH Запрос на обновление пользователя по id-{}", userId);
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public HttpStatus deleteUserById(@PathVariable Long userId) {
        log.info("DELETE Запрос на удаление пользователя по id-{}", userId);
        userService.deleteUserById(userId);
        return HttpStatus.OK;
    }
}
