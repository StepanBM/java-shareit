package ru.practicum.shareit.booking;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.exceptions.AccessDeniedException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.booking.BookingMapper.*;
import static ru.practicum.shareit.booking.BookingMapper.mapToBookingDto;

@Qualifier("BookingDbService")
@Service
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public BookingServiceImpl(ItemRepository itemRepository,
                              UserRepository userRepository,
                              BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public BookingDto addBooking(long userId, NewBookingRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    //log.warn("Ошибка при поиске пользователя. Пользователь с id={} не найден", userId);
                    return new NotFoundException("Пользователь с id=" + userId + " не найден");
                });
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> {
                   // log.warn("Ошибка при поиске вещи. Вещь с id={} не найдена", request.getItemId());
                    return new NotFoundException("Вещь с id=" + request.getItemId() + " не найдена");
                });
        Booking booking = mapToBooking(request);
        booking.setItem(item);
        booking.setBooker(user);
        booking = bookingRepository.save(booking);
        if (!booking.getItem().getAvailable()) {
            throw new ValidationException("Вещь с id=" + request.getItemId() + " недоступна для бронирования", "Запрет на бронирование");
        }
        return mapToBookingDto(booking);
    }

    public List<BookingDto> findAllBooking(long userId, BookingState state) {
        userRepository.findById(userId)
                .orElseThrow(() -> {
                   // log.warn("Ошибка при поиске пользователя. Пользователь с id={} не найден", userId);
                    return new NotFoundException("Пользователь с id=" + userId + " не найден");
                });
        List<Booking> bookings = new ArrayList<>();
        if (state == BookingState.ALL) {
            bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
        } else if (state == BookingState.CURRENT) {
            bookings = bookingRepository.findByBookerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
        } else if (state == BookingState.FUTURE) {
            bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
        } else if (state == BookingState.PAST) {
            bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
        } else if (state == BookingState.WAITING) {
            bookings = bookingRepository.findByBookerIdAndStatusEqualsOrderByStartDesc(userId, BookingStatus.WAITING);
        } else if (state == BookingState.REJECTED) {
            bookings = bookingRepository.findByBookerIdAndStatusEqualsOrderByStartDesc(userId, BookingStatus.REJECTED);
        } else {
            throw new ValidationException("Нет такого состояния фильтра", "Фильтр не найден");
        }
        return bookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .toList();
    }

    @Transactional
    public BookingDto updateBooking(long userId, long bookingId, boolean approved) {
        userRepository.findById(userId)
                .orElseThrow(() -> {
                   // log.warn("Ошибка при поиске пользователя. Пользователь с id={} не найден", userId);
                    return new UserNotFoundException("Пользователь с id=" + userId + " не найден");
                });

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                   // log.warn("Ошибка при поиске бронирования. Бронирование с id={} не найдено", bookingId);
                    return new NotFoundException("Бронирование с id=" + bookingId + " не найдено");
                });

        // Проверка, что пользователь является владельцем вещи
        if (!(booking.getItem().getOwner().getId() == userId)) {
            throw new AccessDeniedException("Только владелец вещи может подтверждать или отклонять бронирование");
        }

        booking = updateBookingFields(booking, approved);
        bookingRepository.save(booking);

        return mapToBookingDto(booking);
    }

    public BookingDto getBookingId(long userId, long bookingId) {
        userRepository.findById(userId)
                .orElseThrow(() -> {
                   // log.warn("Ошибка при поиске пользователя. Пользователь с id={} не найден", userId);
                    return new NotFoundException("Пользователь с id=" + userId + " не найден");
                });
        return bookingRepository.findById(bookingId)
                .map(BookingMapper::mapToBookingDto)
                .orElseThrow(() -> new NotFoundException("Броненрование не найдено"));
    }

    public List<BookingDto> findAllBookingOwner(long userId, BookingState state) {
        userRepository.findById(userId)
                .orElseThrow(() -> {
                   // log.warn("Ошибка при поиске пользователя. Пользователь с id={} не найден", userId);
                    return new NotFoundException("Пользователь с id=" + userId + " не найден");
                });
        List<Booking> bookings = new ArrayList<>();
        if (state == BookingState.ALL) {
            bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
        } else if (state == BookingState.CURRENT) {
            bookings = bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
        } else if (state == BookingState.FUTURE) {
            bookings = bookingRepository.findByItemOwnerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
        } else if (state == BookingState.PAST) {
            bookings = bookingRepository.findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
        } else if (state == BookingState.WAITING) {
            bookings = bookingRepository.findByItemOwnerIdAndStatusEqualsOrderByStartDesc(userId, BookingStatus.WAITING);
        } else if (state == BookingState.REJECTED) {
            bookings = bookingRepository.findByItemOwnerIdAndStatusEqualsOrderByStartDesc(userId, BookingStatus.REJECTED);
        } else {
            throw new ValidationException("Нет такого состояния фильтра", "Фильтр не найден");
        }
        return bookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .toList();
    }

}
