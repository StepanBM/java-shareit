package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.practicum.shareit.exceptions.UpdateValidation;

@Data
public class UpdateUserRequest {

    @NotBlank(message = "Имя не может быть пустым", groups = UpdateValidation.class)
    private String name;

    @Email(message = "Некорректный формат почты", groups = UpdateValidation.class)
    private String email;

    public boolean hasEmail() {
        return !(email == null || email.isBlank());
    }

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }
}
