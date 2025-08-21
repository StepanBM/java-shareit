package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.CreateValidation;
import ru.practicum.shareit.exceptions.UpdateValidation;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@RestController
@RequestMapping("/users")
@Validated
@Slf4j
public class UserController {
    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto addUser(@Validated(CreateValidation.class) @RequestBody NewUserRequest request) {
        log.info("Добавляется пользователь");
        return userService.addUser(request);
    }

    @GetMapping
    public List<UserDto> findAllUsers() {
        log.info("Запрошен список всех пользователей");
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUserId(@PathVariable("id") Long userId) {
        log.info("Запрошена информация по пользователю id={}", userId);
        return userService.getUserId(userId);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@Validated(UpdateValidation.class) @PathVariable("id") Long userId, @RequestBody UpdateUserRequest request) {
        log.info("Обновляется пользователь {}", userId);
        return userService.updateUser(userId, request);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") Long userId) {
        log.info("Удаляется пользователь id={} ", userId);
        userService.deleteUser(userId);
        log.info("Пользователь с  id={} удален", userId);
    }

}
