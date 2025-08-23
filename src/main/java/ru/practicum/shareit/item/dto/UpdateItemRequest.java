package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.exceptions.UpdateValidation;

@Data
public class UpdateItemRequest {
    private long id;

    @NotBlank(message = "Название не может быть пустым", groups = UpdateValidation.class)
    private String name;

    @NotBlank(message = "Описание не может быть пустым", groups = UpdateValidation.class)
    private String description;

    @NotNull(message = "Статус не может быть пустым", groups = UpdateValidation.class)
    private Boolean available;

    private Long ownerId;

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }

    public boolean hasDescription() {
        return !(description == null || description.isBlank());
    }

    public boolean hasAvailable() {
        return !(available == null);
    }
}
