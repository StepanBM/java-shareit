package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.validation.CreateValidation;

import java.time.LocalDateTime;

@Data
public class NewBookingRequest {

    @NotNull(message = "Время начала не может быть null", groups = CreateValidation.class)
    @FutureOrPresent(message = "Время начала не может быть в прошедшев времени", groups = CreateValidation.class)
    private LocalDateTime start;

    @NotNull(message = "Время окнчания не может быть null", groups = CreateValidation.class)
    @FutureOrPresent(message = "Время окончания не может быть в прошедшем времени", groups = CreateValidation.class)
    private LocalDateTime end;

    @NotNull(message = "id вещи не может быть пустым", groups = CreateValidation.class)
    private Long itemId;
}
