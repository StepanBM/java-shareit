package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;

import java.util.List;

public interface BookingService {

    List<BookingDto> findAllBooking(long userId, BookingState state);

    BookingDto addBooking(long userId, NewBookingRequest request);

    BookingDto updateBooking(long userId, long bookingId, boolean approved);

    BookingDto getBookingId(long userId, long bookingId);

    List<BookingDto> findAllBookingOwner(long userId, BookingState state);
}
