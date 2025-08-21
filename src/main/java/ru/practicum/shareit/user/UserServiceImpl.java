package ru.practicum.shareit.user;

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

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepositoryImpl userRepository;

    @Autowired
    public UserServiceImpl(@Qualifier("userRepository") UserRepositoryImpl userRepository) {
        this.userRepository = userRepository;
    }

    @Qualifier
    public List<UserDto> findAllUsers() {
        return userRepository.findAllUsers().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    @Qualifier
    public UserDto addUser(NewUserRequest request) {
        log.debug("Начинается добавление пользователя по запросу {}", request);
        User user = mapToUser(request);
        log.debug("Запрос на добавление пользователя конвертирован в объект класса User {}", user);
        user = userRepository.addUser(user);
        log.debug("Добавлен пользователь {}", user);
        return mapToUserDto(user);
    }

    @Qualifier
    public UserDto getUserId(Long userId) {
        return mapToUserDto(userRepository.getUserId(userId));
    }

    @Qualifier
    public UserDto updateUser(Long userId, UpdateUserRequest request) {
        log.debug("Начинается обновление пользователя по запросу {}", request);
        User updatedUser = userRepository.updateUser(userId, request);
        log.debug("Обновлён пользователь {}", updatedUser);
        return mapToUserDto(updatedUser);
    }

    @Qualifier
    public void deleteUser(Long id) {
        if (userRepository.getUserId(id) == null) {
            log.warn("Ошибка при запросе удаления пользователя. Пользователь с id={} не найден", id);
            throw new NotFoundException("Пользователь с id=" + id + " не найден");
        }
        log.info("Пользователь {} успешно удалён.", id);
        userRepository.deleteUser(id);
    }

}
