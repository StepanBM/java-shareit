package ru.practicum.shareit.user;

import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.validation.CreateValidation;
import ru.practicum.shareit.validation.UpdateValidation;

@RestController
@Validated
@Slf4j
@RequestMapping("/users")
public class UserController {

    private final UserClient userClient;

    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @PostMapping
    public ResponseEntity<Object> addUser(@Validated(CreateValidation.class) @RequestBody NewUserRequest request) {
        log.info("Добавляется пользователь");
        return userClient.addUser(request);
    }

    @GetMapping
    public ResponseEntity<Object> findAllUsers() {
        log.info("Запрошен список всех пользователей");
        return userClient.findAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserId(@Positive @PathVariable("id") Long userId) {
        log.info("Запрошена информация по пользователю id={}", userId);
        return userClient.getUserId(userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@Positive @PathVariable("id") Long userId,
                                             @Validated(UpdateValidation.class) @RequestBody UpdateUserRequest request) {
        log.info("Обновляется пользователь {}", userId);
        return userClient.updateUser(userId, request);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@Positive @PathVariable("id") Long userId) {
        log.info("Удаляется пользователь id={} ", userId);
        userClient.deleteUser(userId);
    }
}

