package ru.practicum.shareit.booking;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(properties = {"spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:shareit"})
public class BookingIntegrationTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingService bookingService;

    private BookingController bookingController;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    public void setup() {
        bookingController = new BookingController(bookingService);

        // Пользователь являющийся владельцем вещи
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@mail.com");
        owner = userRepository.save(owner);

        // Пользователь являющийся закзчиком
        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@mail.com");
        booker = userRepository.save(booker);

        // Вещь
        item = new Item();
        item.setName("Item1");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);
    }

    @Test
    public void addBookingIntegrationTest() {

        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(item.getId());
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto bookingDto = bookingService.addBooking(booker.getId(), request);

        assertNotNull(bookingDto);
        assertEquals(BookingStatus.WAITING, bookingDto.getStatus());
        assertEquals(item.getId(), bookingDto.getItem().getId());
        assertEquals(booker.getId(), bookingDto.getBooker().getId());

        Optional<Booking> bookingOpt = bookingRepository.findById(bookingDto.getId());
        assertTrue(bookingOpt.isPresent());
        Booking booking = bookingOpt.get();
        assertEquals(BookingStatus.WAITING, booking.getStatus());
        assertEquals(item.getId(), booking.getItem().getId());

        assertThrows(NotFoundException.class, () ->
                bookingService.addBooking(999L, request));
    }

//    @Test
//  //  @DisplayName("Попытка добавить бронирование, когда вещь недоступна")
//    public void testAddBooking_ItemNotAvailable() {
//        item.setAvailable(false);
//        itemRepository.save(item);
//
//        NewBookingRequest request = new NewBookingRequest();
//        request.setItemId(item.getId());
//        request.setStart(LocalDateTime.now().plusDays(1));
//        request.setEnd(LocalDateTime.now().plusDays(2));
//        request.setId(2L);
//
//        // Ожидаем, что вызов выбросит ValidationException
//        ValidationException thrown = assertThrows(ValidationException.class, () ->
//                bookingService.addBooking(booker.getId(), request));
//
//        // Убедимся, что исключение было выброшено и содержит ожидаемое сообщение
//        assertNotNull(thrown.getMessage(), "Сообщение об ошибке не должно быть null");
//        assertTrue(thrown.getMessage().contains("недоступна для бронирования"));
//    }

    @Test
    public void updateBookingStatusIntegrationTest() {

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);

        BookingDto bookingDto = bookingService.updateBooking(owner.getId(), booking.getId(), true);

        assertNotNull(bookingDto);
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());

        Optional<Booking> updatedOpt = bookingRepository.findById(booking.getId());
        assertEquals(BookingStatus.APPROVED, updatedOpt.get().getStatus());

        // Другой пользователь пытается подтвердить
        long otherUserId = owner.getId() + 999;

        assertThrows(UserNotFoundException.class, () ->
                bookingService.updateBooking(otherUserId, booking.getId(), true));
    }

    @Test
    public void updateBookingStatusRejectIntegrationTest() {

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);

        BookingDto bookingDto = bookingService.updateBooking(owner.getId(), booking.getId(), false);

        assertNotNull(bookingDto);
        assertEquals(BookingStatus.REJECTED, bookingDto.getStatus());

        Optional<Booking> updatedOpt = bookingRepository.findById(booking.getId());
        assertEquals(BookingStatus.REJECTED, updatedOpt.get().getStatus());
    }

    @Test
    public void getBookingIntegrationTest() {

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        booking = bookingRepository.save(booking);

        BookingDto bookingDto = bookingService.getBookingId(booker.getId(), booking.getId());

        assertNotNull(bookingDto);
        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
        assertEquals(booking.getItem().getId(), bookingDto.getItem().getId());
        assertEquals(booking.getBooker().getId(), bookingDto.getBooker().getId());

        assertThrows(NotFoundException.class, () ->
                bookingService.getBookingId(booker.getId(), 999L));
    }

    // Прошедшее бронирование
    @Test
    public void findAllBookingStateREJECTEDIntegrationTest() {

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(3));
        booking.setEnd(LocalDateTime.now().minusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(booking);

        List<BookingDto> listBookingDto = bookingService.findAllBooking(booker.getId(), BookingState.PAST);

        assertNotNull(listBookingDto);
        assertEquals(BookingStatus.REJECTED, listBookingDto.get(0).getStatus());

        for (BookingDto dto : listBookingDto) {
            assertTrue(dto.getEnd().isBefore(LocalDateTime.now()));
        }

        assertEquals(1, listBookingDto.size());
    }

    // Текущее бронирование
    @Test
    public void findAllBookingStateCurrentIntegrationTest() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        Booking booking2 = new Booking();
        booking2.setStart(LocalDateTime.now().minusDays(1));
        booking2.setEnd(LocalDateTime.now().plusDays(1));
        booking2.setItem(item);
        booking2.setBooker(booker);
        booking2.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking2);

        List<BookingDto> listBookingDto = bookingService.findAllBooking(booker.getId(), BookingState.CURRENT);

        assertNotNull(listBookingDto);
        assertEquals(BookingStatus.APPROVED, listBookingDto.get(0).getStatus());
        assertEquals(BookingStatus.WAITING, listBookingDto.get(1).getStatus());

        for (BookingDto dto : listBookingDto) {
            assertTrue(dto.getStart().isBefore(LocalDateTime.now()) || dto.getStart().isEqual(LocalDateTime.now()));
            assertTrue(dto.getEnd().isAfter(LocalDateTime.now()) || dto.getEnd().isEqual(LocalDateTime.now()));
        }

        assertEquals(2, listBookingDto.size());
    }

    // Будущее бронирование
    @Test
    public void findAllBookingStateFutureIntegrationTest() {

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        List<BookingDto> listBookingDto = bookingService.findAllBooking(booker.getId(), BookingState.FUTURE);

        assertNotNull(listBookingDto);
        assertEquals(BookingStatus.APPROVED, listBookingDto.get(0).getStatus());

        for (BookingDto dto : listBookingDto) {
            assertTrue(dto.getStart().isAfter(LocalDateTime.now()));
        }

        assertEquals(1, listBookingDto.size());
    }

    // Ожидание подтверждения бронирования
    @Test
    public void findAllBookingStateWaitingIntegrationTest() {

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);

        Booking booking2 = new Booking();
        booking2.setStart(LocalDateTime.now().minusDays(1));
        booking2.setEnd(LocalDateTime.now().plusDays(1));
        booking2.setItem(item);
        booking2.setBooker(booker);
        booking2.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking2);

        List<BookingDto> listBookingDto = bookingService.findAllBooking(booker.getId(), BookingState.WAITING);

        assertNotNull(listBookingDto);
        assertEquals(BookingStatus.WAITING, listBookingDto.get(0).getStatus());
        assertEquals(BookingStatus.WAITING, listBookingDto.get(1).getStatus());

        for (BookingDto dto : listBookingDto) {
            assertEquals(BookingStatus.WAITING, dto.getStatus());
        }

        assertEquals(2, listBookingDto.size());
    }


    // Отклонённые бронирования
    @Test
    public void findAllBookingStateRejectedIntegrationTest() {

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(3));
        booking.setEnd(LocalDateTime.now().minusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(booking);

        List<BookingDto> listBookingDto = bookingService.findAllBooking(booker.getId(), BookingState.REJECTED);

        assertNotNull(listBookingDto);
        assertEquals(BookingStatus.REJECTED, listBookingDto.get(0).getStatus());
        assertEquals(1, listBookingDto.size());
    }

    // Все бронирования пользователя
    @Test
    public void findAllBookingStateAllIntegrationTest() {

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        Booking booking2 = new Booking();
        booking2.setStart(LocalDateTime.now().minusDays(1));
        booking2.setEnd(LocalDateTime.now().plusDays(1));
        booking2.setItem(item);
        booking2.setBooker(booker);
        booking2.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking2);

        List<BookingDto> listBookingDto = bookingService.findAllBooking(booker.getId(), BookingState.ALL);

        assertNotNull(listBookingDto);
        assertEquals(BookingStatus.APPROVED, listBookingDto.get(0).getStatus());
        assertEquals(BookingStatus.WAITING, listBookingDto.get(1).getStatus());
        assertEquals(2, listBookingDto.size());
    }

    // Все бронирования владельца
    @Test
    public void findAllBookingOwnerStatusAllIntegrationTest() {

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        Booking booking2 = new Booking();
        booking2.setStart(LocalDateTime.now().minusDays(1));
        booking2.setEnd(LocalDateTime.now().plusDays(1));
        booking2.setItem(item);
        booking2.setBooker(booker);
        booking2.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking2);

        List<BookingDto> listBookingDto = bookingService.findAllBookingOwner(owner.getId(), BookingState.ALL);

        assertNotNull(listBookingDto);
        assertEquals(BookingStatus.APPROVED, listBookingDto.get(0).getStatus());
        assertEquals(BookingStatus.WAITING, listBookingDto.get(1).getStatus());
        assertEquals(2, listBookingDto.size());
    }

}
