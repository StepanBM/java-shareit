package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.practicum.shareit.exceptions.CreateValidation;

@Data
public class NewUserRequest {

    @NotBlank(message = "Имя не может быть пустым", groups = CreateValidation.class)
    private String name;

    @Email(message = "Некорректный формат почты", groups = CreateValidation.class)
    @NotBlank(message = "Почта не может быть пустой", groups = CreateValidation.class)
    private String email;
}
