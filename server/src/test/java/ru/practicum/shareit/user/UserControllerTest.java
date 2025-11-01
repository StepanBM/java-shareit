package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    private UserDto user1;
    private UserDto user2;

    @BeforeEach
    void setUp() {
        user1 = new UserDto();
        user1.setId(1L);
        user1.setName("Sergey");
        user1.setEmail("ser01@mail.com");

        user2 = new UserDto();
        user2.setId(2L);
        user2.setName("Anna");
        user2.setEmail("ann1977@mail.com");
    }

    // Создание пользователя
    @Test
    void addUserTest() throws Exception {

        NewUserRequest request = new NewUserRequest();
        request.setName("Sergey");
        request.setEmail("ser01@mail.com");

        Mockito
                .when(userService.addUser(any())).thenReturn(user1);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Sergey"))
                .andExpect(jsonPath("$.email").value("ser01@mail.com"));
    }

    // Запрос на список всех пользователей
    @Test
    void findAllUsersTest() throws Exception {
        Mockito
                .when(userService.findAllUsers()).thenReturn(List.of(user1, user2));

        mvc.perform(get("/users")
                .content(mapper.writeValueAsString(user1))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is((int) user1.getId().longValue())))
                .andExpect(jsonPath("$[0].name", is(user1.getName())))
                .andExpect(jsonPath("$[0].email", is(user1.getEmail())))
                .andExpect(jsonPath("$[1].id", is((int) user2.getId().longValue())))
                .andExpect(jsonPath("$[1].name", is(user2.getName())))
                .andExpect(jsonPath("$[1].email", is(user2.getEmail())));
    }

    // Вывод пользователя по id
    @Test
    void getUserByIdTest() throws Exception {

        Mockito
                .when(userService.getUserId(1L)).thenReturn(user1);

        mvc.perform(get("/users/1")
                .content(mapper.writeValueAsString(user1))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Sergey"))
                .andExpect(jsonPath("$.email").value("ser01@mail.com"));
    }

    @Test
    void updateUserTest() throws Exception {

        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setName("Sergey");
        updateRequest.setEmail("ser01@mail.com");


        Mockito
                .when(userService.updateUser(anyLong(), any(UpdateUserRequest.class))).thenReturn(user1);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(updateRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sergey"))
                .andExpect(jsonPath("$.email").value("ser01@mail.com"));
    }

    @Test
    void deleteUserTest() throws Exception {

        mvc.perform(delete("/users/1")
                        .content(mapper.writeValueAsString(user1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
