package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.DuplicatedDataException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("userRepository")
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();

    @Qualifier
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Qualifier
    public User addUser(User user) {
        checkEmail(user.getEmail());
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Qualifier
    public User getUserId(Long userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        return user;
    }

    @Qualifier
    public User updateUser(Long userId, UpdateUserRequest request) {

        User user = getUserId(userId);

//        Проверка условия:
//        !request.getEmail().equals(user.getEmail()): проверка, отличается ли новое значение email от текущего.
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            checkEmail(request.getEmail());
            user.setEmail(request.getEmail());
        }
        if (request.getName() != null) {
            user.setName(request.getName());
        }

        return user;
    }

    @Qualifier
    public void deleteUser(Long userId) {
        users.remove(userId);
    }

    private void checkEmail(String email) {
        User user = users.values().stream()
                .filter(us -> us.getEmail().equals(email))
                .findFirst().orElse(null);
        if (user != null) {
            throw new DuplicatedDataException("Пользователь с данным email=" + email + " уже существует");
        }
    }

    // Вспомогательный метод для генерации идентификатора
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
