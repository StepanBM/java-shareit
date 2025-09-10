package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.practicum.shareit.exceptions.CreateValidation;

@Data
public class NewCommentRequest {

    private Long userId;
    private Long itemId;

    @NotBlank(message = "Комментарий не может быть пустым", groups = CreateValidation.class)
    private String text;
}
