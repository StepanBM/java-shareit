package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.practicum.shareit.validation.CreateValidation;

@Data
public class NewUserRequest {

    @NotBlank(message = "Имя не может быть пустым", groups = CreateValidation.class)
    @Length(max = 255, message = "Имя пользователя не может привышать длину в 255 символов", groups = CreateValidation.class)
    private String name;

    @Email(message = "Некорректный формат почты", groups = CreateValidation.class)
    @NotBlank(message = "Почта не может быть пустой", groups = CreateValidation.class)
    @Length(max = 512, message = "Email пользователя не может превышать длину в 512 символов", groups = CreateValidation.class)
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
