package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.exceptions.CreateValidation;

@Data
public class NewItemRequest {

    @NotBlank(message = "Название не может быть пустым", groups = CreateValidation.class)
    private String name;

    @NotBlank(message = "Описание не может быть пустым", groups = CreateValidation.class)
    private String description;

    @NotNull(message = "Статус не может быть пустым", groups = CreateValidation.class)
    private Boolean available;

    private Long ownerId;
}
