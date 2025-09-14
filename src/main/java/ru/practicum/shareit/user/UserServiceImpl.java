package ru.practicum.shareit.user;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static ru.practicum.shareit.user.UserMapper.mapToUser;
import static ru.practicum.shareit.user.UserMapper.mapToUserDto;

@Qualifier("UserDbService")
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    @Override
    @Transactional
    public UserDto addUser(NewUserRequest request) {
        log.debug("Начинается добавление пользователя по запросу {}", request);
        User user = mapToUser(request);
        log.debug("Запрос на добавление пользователя конвертирован в объект класса User {}", user);
        user = userRepository.save(user);
        log.debug("Добавлен пользователь {}", user);
        return mapToUserDto(user);
    }

    @Override
    public UserDto getUserId(Long userId) {
        return userRepository.findById(userId)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> {
                    log.warn("Ошибка при поиске пользователя. Пользователь с id={} не найден", userId);
                    return new NotFoundException("Пользователь с id=" + userId + " не найден");
                });
    }

    @Override
    @Transactional
    public UserDto updateUser(Long userId, UpdateUserRequest request) {
        log.debug("Начинается обновление пользователя по запросу {}", request);
        User updatedUser = userRepository.findById(userId)
                .map(user -> UserMapper.updateUserFields(user, request))
                .orElseThrow(() -> {
                    log.warn("Ошибка при обновлении пользователя. Пользователь с id={} не найден", userId);
                    return new NotFoundException("Пользователь с id=" + userId + " не найден");
                });
        log.debug("Запрос на обновление пользователя конвертирован в объект класса User {}", updatedUser);
        updatedUser = userRepository.save(updatedUser);
        log.debug("Обновлён пользователь {}", updatedUser);
        return mapToUserDto(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        log.info("Пользователь {} успешно удалён.", id);
    }

}
