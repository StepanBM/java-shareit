package ru.practicum.shareit.booking;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.validation.CreateValidation;


@RestController
@Validated
@Slf4j
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingClient bookingClient;

    public BookingController(BookingClient bookingClient) {
        this.bookingClient = bookingClient;
    }


    @PostMapping()
    public ResponseEntity<Object> addBooking(@Positive @NotNull @RequestHeader(name = "X-Sharer-User-Id") long userId,
                                             @Validated(CreateValidation.class) @RequestBody NewBookingRequest request) {
        log.info("Добавление бронирования");
        return bookingClient.addBooking(userId, request);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBookingStatus(@Positive @NotNull @RequestHeader(name = "X-Sharer-User-Id") long userId,
                                                      @Positive @PathVariable("bookingId") long bookingId,
                                                      @RequestParam boolean approved) {
        log.info("Обновление статуса вещи id={}", bookingId);
        return bookingClient.updateBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@Positive @NotNull @RequestHeader(name = "X-Sharer-User-Id") long userId,
                                             @Positive @PathVariable("bookingId") long bookingId) {
        log.info("Запрошена информация о вещи id={}", bookingId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping()
    public ResponseEntity<Object> findAllBooking(@Positive @NotNull @RequestHeader(name = "X-Sharer-User-Id") long userId,
                                                         @RequestParam(defaultValue = "ALL") BookingState state) {

        log.info("Запрошен список всех бронирований пользователя с id={}", userId);
        return bookingClient.findAllBooking(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllBookingOwner(@Positive @NotNull @RequestHeader(name = "X-Sharer-User-Id") long userId,
                                                         @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info("Запрошен список бронирований всех вещей пользователя с id={}", userId);
        return bookingClient.findAllBookingOwner(userId, state);
    }
}
