package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.dto.UpdateBookingRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingDtoJsonTest {

    private final JacksonTester<BookingDto> jsonBookingDto;
    private final JacksonTester<NewBookingRequest> jsonNewBooking;
    private final JacksonTester<UpdateBookingRequest> jsonUpdBooking;

    @Test
    public void serializeBookingDtoJsonTest() throws Exception {

        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName("Печатная машинка");
        item.setDescription("Очень хорошая");
        item.setAvailable(true);
        item.setRequestId(5L);
        item.setOwnerId(3L);

        UserDto user = new UserDto();
        user.setId(7L);
        user.setName("Anna");
        user.setEmail("anna@mail.com");

        BookingDto booking = new BookingDto();
        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2025, 5, 21, 14, 0));
        booking.setEnd(LocalDateTime.of(2025, 7, 21, 14, 0));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.APPROVED);

        JsonContent<BookingDto> json = jsonBookingDto.write(booking);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.start").isEqualTo("2025-05-21T14:00:00");
        assertThat(json).extractingJsonPathStringValue("$.end").isEqualTo("2025-07-21T14:00:00");
        assertThat(json).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
    }

    @Test
    public void deserializeJsonToBookingDtoJsonTest() throws Exception {
        String json = """
                {
                  "id": 1,
                  "start": "2025-05-21T14:00:00",
                  "end": "2025-07-21T14:00:00",
                  "item": {
                    "id": 12,
                    "name": "Принтер",
                    "description": "Быстро печатает",
                    "available": true,
                    "requestId": 4,
                    "ownerId": 3
                  },
                  "booker": {
                    "id": 10,
                    "name": "Jane Doe",
                    "email": "jane@example.com"
                  },
                  "status": "APPROVED"
                }
                """;

        BookingDto dto = jsonBookingDto.parse(json).getObject();

        assertEquals(1L, dto.getId());
        assertEquals(LocalDateTime.of(2025, 5, 21, 14, 0), dto.getStart());
        assertEquals(LocalDateTime.of(2025, 7, 21, 14, 0), dto.getEnd());
        assertEquals(BookingStatus.APPROVED, dto.getStatus());

        assertEquals(12L, dto.getItem().getId());
        assertEquals("Принтер", dto.getItem().getName());
        assertEquals("Быстро печатает", dto.getItem().getDescription());
        assertTrue(dto.getItem().getAvailable());
        assertEquals(4L, dto.getItem().getRequestId());
        assertEquals(3L, dto.getItem().getOwnerId());

        assertEquals("Jane Doe", dto.getBooker().getName());
        assertEquals("jane@example.com", dto.getBooker().getEmail());
    }

    @Test
    public void serializeNewBookingRequestJsonTest() throws Exception {
        NewBookingRequest newBooking = new NewBookingRequest();
        newBooking.setStart(LocalDateTime.of(2025, 5, 21, 14, 0));
        newBooking.setEnd(LocalDateTime.of(2025, 7, 21, 14, 0));
        newBooking.setItemId(5L);

        JsonContent<NewBookingRequest> json = jsonNewBooking.write(newBooking);

        assertThat(json).extractingJsonPathNumberValue("$.itemId").isEqualTo(5);
        assertThat(json).extractingJsonPathStringValue("$.start").isEqualTo("2025-05-21T14:00:00");
        assertThat(json).extractingJsonPathStringValue("$.end").isEqualTo("2025-07-21T14:00:00");
    }

    @Test
    public void deserializeJsonToNewBookingRequestJsonTest() throws Exception {
        String json = """
                {
                  "start": "2025-05-21T14:00:00",
                  "end": "2025-07-21T14:00:00",
                  "itemId": 25
                }
                """;

        NewBookingRequest dto = jsonNewBooking.parse(json).getObject();

        assertEquals(LocalDateTime.of(2025, 5, 21, 14, 0), dto.getStart());
        assertEquals(LocalDateTime.of(2025, 7, 21, 14, 0), dto.getEnd());
        assertEquals(25L, dto.getItemId());
    }

    @Test
    public void serializeUpdateBookingRequestJsonTest() throws Exception {
        UpdateBookingRequest updateRequest = new UpdateBookingRequest();
        updateRequest.setUserId(3L);
        updateRequest.setBookingId(17L);
        updateRequest.setApproved(true);

        JsonContent<UpdateBookingRequest> json = jsonUpdBooking.write(updateRequest);

        assertThat(json).extractingJsonPathNumberValue("$.userId").isEqualTo(3);
        assertThat(json).extractingJsonPathNumberValue("$.bookingId").isEqualTo(17);
        assertThat(json).extractingJsonPathBooleanValue("$.approved").isTrue();
    }

    @Test
    public void deserializeJsonToUpdateBookingRequestJsonTest() throws Exception {
        String json = """
                {
                  "userId": 3,
                  "bookingId": 15,
                  "approved": true
                }
                """;

        UpdateBookingRequest dto = jsonUpdBooking.parse(json).getObject();

        assertEquals(3L, dto.getUserId());
        assertEquals(15L, dto.getBookingId());
        assertTrue(dto.isApproved());
    }
}
