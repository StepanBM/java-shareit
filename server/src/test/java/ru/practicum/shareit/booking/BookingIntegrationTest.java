package ru.practicum.shareit.booking;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.exceptions.AccessDeniedException;
import ru.practicum.shareit.exceptions.ItemUnavailableException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Test
    public void addBookingUserNotFoundIntegrationTest() {

        NewBookingRequest request = new NewBookingRequest();
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));
        request.setItemId(item.getId());

        assertThrows(NotFoundException.class, () -> {
            bookingService.addBooking(999L, request);
        });
    }

    @Test
    public void addBookingItemNotFoundIntegrationTest() {

        NewBookingRequest request = new NewBookingRequest();
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));
        request.setItemId(999L);

        assertThrows(NotFoundException.class, () -> {
            bookingService.addBooking(booker.getId(), request);
        });
    }

    @Test
    public void addBookingItemNotAvailableIntegrationTest() {
        item.setAvailable(false);
        itemRepository.save(item);

        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(item.getId());
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        assertThrows(ItemUnavailableException.class, () -> {
            bookingService.addBooking(booker.getId(), request);
        });
    }

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

        assertThrows(NotFoundException.class, () ->
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
    public void updateBookingUserNotFoundIntegrationTest() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);

        assertThrows(NotFoundException.class, () -> {
            bookingService.updateBooking(999L, booking.getId(), true);
        });
    }

    @Test
    public void updateBookingNotFoundIntegrationTest() {

        assertThrows(NotFoundException.class, () -> {
            bookingService.updateBooking(owner.getId(), 999L, true);
        });
    }

    @Test
    public void updateBookingAccessDeniedIntegrationTest() {

        User otherUser = new User();
        otherUser.setName("Marina");
        otherUser.setEmail("Marrrrr@mail.com");
        otherUser = userRepository.save(otherUser);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);

        User finalOtherUser = otherUser;
        assertThrows(AccessDeniedException.class, () -> {
            bookingService.updateBooking(finalOtherUser.getId(), booking.getId(), true);
        });
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

    }

    @Test
    public void getBookingNotFoundIntegrationTest() {

        assertThrows(NotFoundException.class, () -> {
            bookingService.getBookingId(booker.getId(), 999L);
        });
    }

    @Test
    public void getBookingUserNotFoundIntegrationTest() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        booking = bookingRepository.save(booking);

        Booking finalBooking = booking;
        assertThrows(NotFoundException.class, () -> {
            bookingService.getBookingId(999L, finalBooking.getId());
        });
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

    @Test
    public void findAllBookingUserNotFoundIntegrationTest() {

        assertThrows(NotFoundException.class, () ->
                bookingService.findAllBooking(999L, BookingState.ALL)
        );
    }

    @Test
    public void findAllBookingEmptyResultIntegrationTest() {

        User newUser = new User();
        newUser.setName("New");
        newUser.setEmail("new@mail.com");
        newUser = userRepository.save(newUser);

        List<BookingDto> result = bookingService.findAllBooking(newUser.getId(), BookingState.ALL);

        assertTrue(result.isEmpty());
    }

    @Test
    public void findAllBookingOwnerAllStatesIntegrationTest() {

        LocalDateTime now = LocalDateTime.now();

        // Текущее бронирование
        Booking currentBooking = new Booking();
        currentBooking.setStart(now.minusDays(1));
        currentBooking.setEnd(now.plusDays(1));
        currentBooking.setItem(item);
        currentBooking.setBooker(booker);
        currentBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(currentBooking);

        // Будущее бронирование
        Booking futureBooking = new Booking();
        futureBooking.setStart(now.plusDays(2));
        futureBooking.setEnd(now.plusDays(3));
        futureBooking.setItem(item);
        futureBooking.setBooker(booker);
        futureBooking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(futureBooking);

        // Прошедшее бронирование
        Booking pastBooking = new Booking();
        pastBooking.setStart(now.minusDays(5));
        pastBooking.setEnd(now.minusDays(3));
        pastBooking.setItem(item);
        pastBooking.setBooker(booker);
        pastBooking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(pastBooking);

        // Ожидающее подтверждение бронирование
        Booking waitingBooking = new Booking();
        waitingBooking.setStart(now.plusHours(1));
        waitingBooking.setEnd(now.plusDays(1));
        waitingBooking.setItem(item);
        waitingBooking.setBooker(booker);
        waitingBooking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(waitingBooking);

        // Отклонённое бронирование
        Booking rejectedBooking = new Booking();
        rejectedBooking.setStart(now.minusDays(4));
        rejectedBooking.setEnd(now.minusDays(2));
        rejectedBooking.setItem(item);
        rejectedBooking.setBooker(booker);
        rejectedBooking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(rejectedBooking);

        List<BookingDto> result = bookingService.findAllBookingOwner(owner.getId(), BookingState.ALL);
        assertEquals(5, result.size());

        Set<BookingStatus> statuses = result.stream()
                .map(BookingDto::getStatus)
                .collect(Collectors.toSet());
        assertTrue(statuses.containsAll(Arrays.asList(
                BookingStatus.APPROVED,
                BookingStatus.WAITING,
                BookingStatus.REJECTED
        )));

        assertThrows(NotFoundException.class, () ->
                bookingService.findAllBookingOwner(9999L, BookingState.ALL));
    }

    @Test
    public void findAllBookingOwnerCurrentIntegrationTest() {

        LocalDateTime now = LocalDateTime.now();

        // Текущее бронирование
        Booking currentBooking = new Booking();
        currentBooking.setStart(now.minusDays(1));
        currentBooking.setEnd(now.plusDays(1));
        currentBooking.setItem(item);
        currentBooking.setBooker(booker);
        currentBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(currentBooking);

        // Будущее бронирование
        Booking futureBooking = new Booking();
        futureBooking.setStart(now.plusDays(2));
        futureBooking.setEnd(now.plusDays(3));
        futureBooking.setItem(item);
        futureBooking.setBooker(booker);
        futureBooking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(futureBooking);

        // Прошедшее бронирование
        Booking pastBooking = new Booking();
        pastBooking.setStart(now.minusDays(5));
        pastBooking.setEnd(now.minusDays(3));
        pastBooking.setItem(item);
        pastBooking.setBooker(booker);
        pastBooking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(pastBooking);

        // Ожидающее подтверждение бронирование
        Booking waitingBooking = new Booking();
        waitingBooking.setStart(now.plusHours(1));
        waitingBooking.setEnd(now.plusDays(1));
        waitingBooking.setItem(item);
        waitingBooking.setBooker(booker);
        waitingBooking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(waitingBooking);

        // Отклонённое бронирование
        Booking rejectedBooking = new Booking();
        rejectedBooking.setStart(now.minusDays(4));
        rejectedBooking.setEnd(now.minusDays(2));
        rejectedBooking.setItem(item);
        rejectedBooking.setBooker(booker);
        rejectedBooking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(rejectedBooking);

        List<BookingDto> result = bookingService.findAllBookingOwner(owner.getId(), BookingState.CURRENT);
        assertEquals(1, result.size());
        assertEquals(BookingStatus.APPROVED, result.get(0).getStatus());
    }

    @Test
    public void findAllBookingOwnerFutureIntegrationTest() {

        LocalDateTime now = LocalDateTime.now();

        // Текущее бронирование
        Booking currentBooking = new Booking();
        currentBooking.setStart(now.minusDays(1));
        currentBooking.setEnd(now.plusDays(1));
        currentBooking.setItem(item);
        currentBooking.setBooker(booker);
        currentBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(currentBooking);

        // Будущее бронирование
        Booking futureBooking = new Booking();
        futureBooking.setStart(now.plusDays(2));
        futureBooking.setEnd(now.plusDays(3));
        futureBooking.setItem(item);
        futureBooking.setBooker(booker);
        futureBooking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(futureBooking);

        // Прошедшее бронирование
        Booking pastBooking = new Booking();
        pastBooking.setStart(now.minusDays(5));
        pastBooking.setEnd(now.minusDays(3));
        pastBooking.setItem(item);
        pastBooking.setBooker(booker);
        pastBooking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(pastBooking);

        // Ожидающее подтверждение бронирование
        Booking waitingBooking = new Booking();
        waitingBooking.setStart(now.plusHours(1));
        waitingBooking.setEnd(now.plusDays(1));
        waitingBooking.setItem(item);
        waitingBooking.setBooker(booker);
        waitingBooking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(waitingBooking);

        // Отклонённое бронирование
        Booking rejectedBooking = new Booking();
        rejectedBooking.setStart(now.minusDays(4));
        rejectedBooking.setEnd(now.minusDays(2));
        rejectedBooking.setItem(item);
        rejectedBooking.setBooker(booker);
        rejectedBooking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(rejectedBooking);

        List<BookingDto> result = bookingService.findAllBookingOwner(owner.getId(), BookingState.FUTURE);
        assertEquals(2, result.size());
        assertEquals(BookingStatus.WAITING, result.get(0).getStatus());
    }

    @Test
    public void findAllBookingOwnerPastIntegrationTest() {

        LocalDateTime now = LocalDateTime.now();

        // Текущее бронирование
        Booking currentBooking = new Booking();
        currentBooking.setStart(now.minusDays(1));
        currentBooking.setEnd(now.plusDays(1));
        currentBooking.setItem(item);
        currentBooking.setBooker(booker);
        currentBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(currentBooking);

        // Будущее бронирование
        Booking futureBooking = new Booking();
        futureBooking.setStart(now.plusDays(2));
        futureBooking.setEnd(now.plusDays(3));
        futureBooking.setItem(item);
        futureBooking.setBooker(booker);
        futureBooking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(futureBooking);

        // Прошедшее бронирование
        Booking pastBooking = new Booking();
        pastBooking.setStart(now.minusDays(5));
        pastBooking.setEnd(now.minusDays(3));
        pastBooking.setItem(item);
        pastBooking.setBooker(booker);
        pastBooking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(pastBooking);

        // Ожидающее подтверждение бронирование
        Booking waitingBooking = new Booking();
        waitingBooking.setStart(now.plusHours(1));
        waitingBooking.setEnd(now.plusDays(1));
        waitingBooking.setItem(item);
        waitingBooking.setBooker(booker);
        waitingBooking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(waitingBooking);

        // Отклонённое бронирование
        Booking rejectedBooking = new Booking();
        rejectedBooking.setStart(now.minusDays(4));
        rejectedBooking.setEnd(now.minusDays(2));
        rejectedBooking.setItem(item);
        rejectedBooking.setBooker(booker);
        rejectedBooking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(rejectedBooking);

        List<BookingDto> result = bookingService.findAllBookingOwner(owner.getId(), BookingState.PAST);
        assertEquals(2, result.size());
        assertEquals(BookingStatus.REJECTED, result.get(0).getStatus());
    }

    @Test
    public void findAllBookingOwnerWaitingIntegrationTest() {

        LocalDateTime now = LocalDateTime.now();

        // Текущее бронирование
        Booking currentBooking = new Booking();
        currentBooking.setStart(now.minusDays(1));
        currentBooking.setEnd(now.plusDays(1));
        currentBooking.setItem(item);
        currentBooking.setBooker(booker);
        currentBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(currentBooking);

        // Будущее бронирование
        Booking futureBooking = new Booking();
        futureBooking.setStart(now.plusDays(2));
        futureBooking.setEnd(now.plusDays(3));
        futureBooking.setItem(item);
        futureBooking.setBooker(booker);
        futureBooking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(futureBooking);

        // Прошедшее бронирование
        Booking pastBooking = new Booking();
        pastBooking.setStart(now.minusDays(5));
        pastBooking.setEnd(now.minusDays(3));
        pastBooking.setItem(item);
        pastBooking.setBooker(booker);
        pastBooking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(pastBooking);

        // Ожидающее подтверждение бронирование
        Booking waitingBooking = new Booking();
        waitingBooking.setStart(now.plusHours(1));
        waitingBooking.setEnd(now.plusDays(1));
        waitingBooking.setItem(item);
        waitingBooking.setBooker(booker);
        waitingBooking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(waitingBooking);

        // Отклонённое бронирование
        Booking rejectedBooking = new Booking();
        rejectedBooking.setStart(now.minusDays(4));
        rejectedBooking.setEnd(now.minusDays(2));
        rejectedBooking.setItem(item);
        rejectedBooking.setBooker(booker);
        rejectedBooking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(rejectedBooking);

        List<BookingDto> result = bookingService.findAllBookingOwner(owner.getId(), BookingState.WAITING);
        assertEquals(2, result.size());
        assertEquals(BookingStatus.WAITING, result.get(0).getStatus());
    }

    @Test
    public void findAllBookingOwnerRejectedIntegrationTest() {

        LocalDateTime now = LocalDateTime.now();

        // Текущее бронирование
        Booking currentBooking = new Booking();
        currentBooking.setStart(now.minusDays(1));
        currentBooking.setEnd(now.plusDays(1));
        currentBooking.setItem(item);
        currentBooking.setBooker(booker);
        currentBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(currentBooking);

        // Будущее бронирование
        Booking futureBooking = new Booking();
        futureBooking.setStart(now.plusDays(2));
        futureBooking.setEnd(now.plusDays(3));
        futureBooking.setItem(item);
        futureBooking.setBooker(booker);
        futureBooking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(futureBooking);

        // Прошедшее бронирование
        Booking pastBooking = new Booking();
        pastBooking.setStart(now.minusDays(5));
        pastBooking.setEnd(now.minusDays(3));
        pastBooking.setItem(item);
        pastBooking.setBooker(booker);
        pastBooking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(pastBooking);

        // Ожидающее подтверждение бронирование
        Booking waitingBooking = new Booking();
        waitingBooking.setStart(now.plusHours(1));
        waitingBooking.setEnd(now.plusDays(1));
        waitingBooking.setItem(item);
        waitingBooking.setBooker(booker);
        waitingBooking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(waitingBooking);

        // Отклонённое бронирование
        Booking rejectedBooking = new Booking();
        rejectedBooking.setStart(now.minusDays(4));
        rejectedBooking.setEnd(now.minusDays(2));
        rejectedBooking.setItem(item);
        rejectedBooking.setBooker(booker);
        rejectedBooking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(rejectedBooking);

        List<BookingDto> result = bookingService.findAllBookingOwner(owner.getId(), BookingState.REJECTED);
        assertEquals(2, result.size());
        assertEquals(BookingStatus.REJECTED, result.get(0).getStatus());
    }

    @Test
    public void findAllBookingOwnerUserNotFoundIntegrationTest() {

        assertThrows(NotFoundException.class, () -> {
            bookingService.findAllBookingOwner(999L, BookingState.ALL);
        });
    }

    @Test
    public void findAllBookingOwnerEmptyResultIntegrationTest() {
        User newOwner = new User();
        newOwner.setName("Alex");
        newOwner.setEmail("aleeex@mail.com");
        newOwner = userRepository.save(newOwner);

        List<BookingDto> bookingDto = bookingService.findAllBookingOwner(newOwner.getId(), BookingState.ALL);

        assertTrue(bookingDto.isEmpty());
    }

}
