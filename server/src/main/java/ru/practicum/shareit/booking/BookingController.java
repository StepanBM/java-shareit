package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

   private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto addBooking(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                 @RequestBody NewBookingRequest request) {
        log.info("Добавление бронирования");
        return bookingService.addBooking(userId, request);
    }


    @PatchMapping("/{bookingId}")
    public BookingDto updateBookingStatus(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                          @PathVariable("bookingId") long bookingId,
                                          @RequestParam boolean approved) {
        log.info("Обновление статуса вещи id={}", bookingId);
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                 @PathVariable("bookingId") long bookingId) {
        log.info("Запрошена информация о вещи id={}", bookingId);
        return bookingService.getBookingId(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> findAllBooking(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                           @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info("Запрошен список всех бронирований пользователя с id={}", userId);
        return bookingService.findAllBooking(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllBookingOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info("Запрошен список бронирований всех вещей пользователя с id={}", userId);
        return bookingService.findAllBookingOwner(userId, state);
    }

}
