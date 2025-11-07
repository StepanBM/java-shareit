package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import ru.practicum.shareit.validation.CreateValidation;

import java.time.LocalDateTime;

@Getter
@Setter
public class NewItemRequestDto {

    private Long requestId;

    @NotBlank(message = "Описание не может быть пустым", groups = CreateValidation.class)
    @Length(max = 512, message = "Текст запроса не может привышать длину в 512 символов", groups = CreateValidation.class)
    private String description;

    @Null(message = "Некорректные данные времени создания запроса")
    private LocalDateTime created;

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }
}
