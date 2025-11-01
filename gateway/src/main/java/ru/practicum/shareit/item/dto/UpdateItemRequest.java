package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.practicum.shareit.validation.UpdateValidation;

@Data
public class UpdateItemRequest {

    private long id;

    @NotBlank(message = "Название не может быть пустым", groups = UpdateValidation.class)
    @Length(max = 255, message = "Название предмета не может привышать длину в 255 символа", groups = UpdateValidation.class)
    private String name;

    @NotBlank(message = "Описание не может быть пустым", groups = UpdateValidation.class)
    @Length(max = 512, message = "Описание предмета не может привышать длину в 512 символов", groups = UpdateValidation.class)
    private String description;

    @NotNull(message = "Статус не может быть пустым", groups = UpdateValidation.class)
    private Boolean available;

    private Long ownerId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}
