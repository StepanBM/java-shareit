package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Validated
@Slf4j
public class BookingController {

    BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto addBooking(@Positive @RequestHeader(name = "X-Sharer-User-Id") long userId,
                                 @Valid @RequestBody NewBookingRequest request) {
        log.info("Добавление бронирования");
        return bookingService.addBooking(userId, request);
    }


    @PatchMapping("/{bookingId}")
    public BookingDto updateBookingStatus(@Positive @RequestHeader(name = "X-Sharer-User-Id") long userId,
                                          @PathVariable("bookingId") long bookingId,
                                          @RequestParam boolean approved) {
        log.info("Обновление статуса вещи id={}", bookingId);
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@Positive @RequestHeader(name = "X-Sharer-User-Id") long userId,
                                 @PathVariable("bookingId") long bookingId) {
        log.info("Запрошена информация о вещи id={}", bookingId);
        return bookingService.getBookingId(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> findAllBooking(@Positive @RequestHeader(name = "X-Sharer-User-Id") long userId,
                                           @RequestParam(required = false, defaultValue = "ALL") BookingState state) {
        log.info("Запрошен список всех бронирований пользователя с id={}", userId);
        return bookingService.findAllBooking(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllBookingOwner(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestParam(required = false, defaultValue = "ALL") BookingState state) {
        log.info("Запрошен список бронирований всех вещей пользователя с id={}", userId);
        return bookingService.findAllBookingOwner(userId, state);
    }

}
