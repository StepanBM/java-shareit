package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.exceptions.CreateValidation;

import java.time.LocalDateTime;

@Data
public class NewBookingRequest {

    @NotBlank(message = "id не может быть пустым", groups = CreateValidation.class)
    Long id;

    @NotNull(message = "Время начала не может быть null", groups = CreateValidation.class)
    LocalDateTime start;

    @NotNull(message = "Время окнчания не может быть null", groups = CreateValidation.class)
    LocalDateTime end;

    @NotNull
    Long itemId;
}
