package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UpdateUserRequest;

import java.util.List;

public interface UserRepository {

    List<User> findAllUsers();

    User addUser(User user);

    User getUserId(Long userId);

    User updateUser(Long userId, UpdateUserRequest request);

    void deleteUser(Long userId);
}
