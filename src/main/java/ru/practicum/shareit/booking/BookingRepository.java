package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Вывод забронированных вещей конкретного пользователя
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    // Вывод текущих активных бронирований
    List<Booking> findByBookerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(Long bookerId, LocalDateTime now, LocalDateTime now2);

    // Вывод будущих бронирований
    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime now);

    // Вывод прошлых бронирований
    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

    // Вывод ожидающих бронирований и вывод отказанных бронирований
    List<Booking> findByBookerIdAndStatusEqualsOrderByStartDesc(Long bookerId, BookingStatus bookingStatus);

    // List<Booking> findByBookerIdAndStatusEqualsOrderByStartDesc(Long bookerId, BookingStatus rejected);

    // Вывод всех бронирований для вещей пользователя
    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);

    // Вывод активных бронирований
    List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long ownerId, LocalDateTime now, LocalDateTime now2);

    // Вывод будущих бронирований
    List<Booking> findByItemOwnerIdAndStartIsAfterOrderByStartDesc(Long ownerId, LocalDateTime now);

    // Вывод прошедших бронирований
    List<Booking> findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(Long ownerId, LocalDateTime now);

    // Вывод ожидающих подтверждения бронирований и вывод отклонённых бронирований
    List<Booking> findByItemOwnerIdAndStatusEqualsOrderByStartDesc(Long ownerId, BookingStatus bookingStatus);

    // List<Booking> findByItemOwnerIdAndStatusEqualsOrderByStartDesc(Long ownerId, BookingStatus rejected);

    boolean existsByItemIdAndBookerIdAndEndBefore(Long itemId, Long bookerId, LocalDateTime dateTime);

    Optional<Booking> findByItemIdAndEndIsAfterOrderByEndDesc(Long itemId, LocalDateTime now);

    Optional<Booking> findByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime now);

}
