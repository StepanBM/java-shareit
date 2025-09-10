package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.exceptions.UpdateValidation;

@Data
public class UpdateBookingRequest {

    @NotNull(message = "Id не может быть пустым", groups = UpdateValidation.class)
    long userId;
    @NotNull(message = "Id не может быть пустым", groups = UpdateValidation.class)
    long bookingId;
    @NotNull(message = "Статус не может быть пустым", groups = UpdateValidation.class)
    boolean approved;
}
