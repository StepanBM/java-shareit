package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.practicum.shareit.validation.UpdateValidation;

@Data
public class UpdateUserRequest {

    @Length(max = 255, message = "Имя пользователя не может привышать длину в 255 символов", groups = UpdateValidation.class)
    private String name;

    @Email(message = "Некорректный формат почты", groups = UpdateValidation.class)
    @Length(max = 512, message = "Email пользователя не может привышать длину в 512 символов", groups = UpdateValidation.class)
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
