package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> findAllUsers();

    UserDto addUser(NewUserRequest request);

    UserDto getUserId(Long userId);

    UserDto updateUser(Long userId, UpdateUserRequest request);

    void deleteUser(Long id);

}
