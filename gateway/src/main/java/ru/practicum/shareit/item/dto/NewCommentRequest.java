package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.practicum.shareit.validation.CreateValidation;

@Data
public class NewCommentRequest {

    private Long userId;
    private Long itemId;

    @NotBlank(message = "Комментарий не может быть пустым", groups = CreateValidation.class)
    @Length(max = 512, message = "Комментарий не может превышать длину в 512 символов", groups = CreateValidation.class)
    private String text;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
