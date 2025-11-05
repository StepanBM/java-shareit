package ru.practicum.shareit.user;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(properties = {"spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:shareit"})
public class UserIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private UserController userController;

    @BeforeEach
    public void setUp() {

        userController = new UserController(userService);

        User user1 = new User();
        user1.setName("Sergey");
        user1.setEmail("ser01@mail.com");
        userRepository.save(user1);

        User user2 = new User();
        user2.setName("Anna");
        user2.setEmail("ann1977@mail.com");
        userRepository.save(user2);
    }

    @Test
    public void addUserIntegrationTest() {

        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setName("New User");
        newUserRequest.setEmail("newuser@mail.com");

        UserDto createdUser = userController.addUser(newUserRequest);

        Assertions.assertNotNull(createdUser);
        Assertions.assertEquals("New User", createdUser.getName());
        Assertions.assertEquals("newuser@mail.com", createdUser.getEmail());

        // Проверка, что пользователь добавлен в базу данных
        User user = userRepository.findById(createdUser.getId()).orElse(null);
        Assertions.assertNotNull(user);
        Assertions.assertEquals("New User", user.getName());
        Assertions.assertEquals("newuser@mail.com", user.getEmail());
    }

    @Test
    public void findAllUsersIntegrationTest() {

        List<UserDto> users = userController.findAllUsers();

        Assertions.assertNotNull(users);
        Assertions.assertEquals(2, users.size(), "Количество пользователей правильное.");

        boolean userName1 = users.stream().anyMatch(u -> u.getName().equals("Sergey"));
        Assertions.assertTrue(userName1, "Данный тестовый пользователь должен быть в списке");

        boolean userName2 = users.stream().anyMatch(u -> u.getName().equals("Anna"));
        Assertions.assertTrue(userName2, "Данный тестовый пользователь должен быть в списке");
        boolean userEmail2 = users.stream().anyMatch(u -> u.getEmail().equals("ann1977@mail.com"));
        Assertions.assertTrue(userEmail2, "Данный тестовый пользователь должен быть в списке");

    }

    @Test
    public void getUserByIdIntegrationTest() {
        User user = userRepository.findAll().get(0);
        UserDto userDto = userController.getUserId(user.getId());

        Assertions.assertNotNull(userDto);
        Assertions.assertEquals(user.getName(), userDto.getName());
        Assertions.assertEquals(user.getEmail(), userDto.getEmail());

        User user2 = userRepository.findAll().get(1);
        UserDto userDto2 = userController.getUserId(user2.getId());

        Assertions.assertNotNull(userDto2);
        Assertions.assertEquals(user2.getName(), userDto2.getName());
        Assertions.assertEquals(user2.getEmail(), userDto2.getEmail());

        assertThrows(NotFoundException.class, () -> {
            userController.getUserId(999L);
        });
    }

    @Test
    public void updateUserIntegrationTest() {
        User user = userRepository.findAll().get(0);
        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setName("Updated Name");
        updateRequest.setEmail("updatedemail@mail.com");

        UserDto updatedUserDto = userController.updateUser(user.getId(), updateRequest);

        Assertions.assertNotNull(updatedUserDto);
        Assertions.assertEquals("Updated Name", updatedUserDto.getName());
        Assertions.assertEquals("updatedemail@mail.com", updatedUserDto.getEmail());

        // Проверка, что пользователь обновлён в базе данных
        User userBd = userRepository.findById(user.getId()).orElse(null);
        Assertions.assertNotNull(userBd);
        Assertions.assertEquals("Updated Name", userBd.getName());
        Assertions.assertEquals("updatedemail@mail.com", userBd.getEmail());

        assertThrows(NotFoundException.class, () -> {
            userController.getUserId(999L);
        });
    }

    @Test
    public void deleteUserIntegrationTest() {
        User user = userRepository.findAll().get(0);
        userController.deleteUser(user.getId());

        // Проверка, что пользователь удален
        boolean exists = userRepository.existsById(user.getId());
        Assertions.assertFalse(exists);
    }

}
