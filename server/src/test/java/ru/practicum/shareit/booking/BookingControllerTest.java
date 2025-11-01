package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookingService bookingService;

    @Test
    public void addBookingTest() throws Exception {
        NewBookingRequest request = new NewBookingRequest();
        request.setStart(LocalDateTime.of(2025, 5, 21, 14, 0));
        request.setEnd(LocalDateTime.of(2025, 7, 21, 14, 0));
        request.setItemId(7L);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(7L);
        itemDto.setName("Велосипед");
        itemDto.setDescription("Очень быстрый");
        itemDto.setAvailable(true);
        itemDto.setOwnerId(15L);
        itemDto.setRequestId(55L);

        UserDto userDto = new UserDto();
        userDto.setId(21L);
        userDto.setName("Sergey");
        userDto.setEmail("serg007@mail.com");

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(request.getStart());
        bookingDto.setEnd(request.getEnd());
        bookingDto.setStatus(BookingStatus.APPROVED);
        bookingDto.setItem(itemDto);
        bookingDto.setBooker(userDto);

        Mockito
                .when(bookingService.addBooking(anyLong(), any(NewBookingRequest.class)))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                .header("X-Sharer-User-Id", "21")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.start").value("2025-05-21T14:00:00"))
                .andExpect(jsonPath("$.end").value("2025-07-21T14:00:00"))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.item.id").value(7))
                .andExpect(jsonPath("$.item.name").value("Велосипед"))
                .andExpect(jsonPath("$.item.description").value("Очень быстрый"))
                .andExpect(jsonPath("$.item.available").value(true))
                .andExpect(jsonPath("$.item.ownerId").value(15))
                .andExpect(jsonPath("$.item.requestId").value(55))
                .andExpect(jsonPath("$.booker.id").value(21))
                .andExpect(jsonPath("$.booker.name").value("Sergey"))
                .andExpect(jsonPath("$.booker.email").value("serg007@mail.com"));

    }

    @Test
    public void updateBookingStatusTest() throws Exception {

        BookingDto responseDto = new BookingDto();
        responseDto.setId(17L);
        responseDto.setStatus(BookingStatus.APPROVED);

        Mockito
                .when(bookingService.updateBooking(1L, 17L, true))
                .thenReturn(responseDto);

        mvc.perform(patch("/bookings/17")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(17))
                .andExpect(jsonPath("$.status").value("APPROVED"));

    }

    @Test
    void getBookingTest() throws Exception {

        NewBookingRequest request = new NewBookingRequest();
        request.setStart(LocalDateTime.of(2025, 5, 21, 14, 0));
        request.setEnd(LocalDateTime.of(2025, 7, 21, 14, 0));
        request.setItemId(7L);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(7L);
        itemDto.setName("Велосипед");
        itemDto.setDescription("Очень быстрый");
        itemDto.setAvailable(true);
        itemDto.setOwnerId(15L);
        itemDto.setRequestId(55L);

        UserDto userDto = new UserDto();
        userDto.setId(21L);
        userDto.setName("Sergey");
        userDto.setEmail("serg007@mail.com");

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(request.getStart());
        bookingDto.setEnd(request.getEnd());
        bookingDto.setStatus(BookingStatus.APPROVED);
        bookingDto.setItem(itemDto);
        bookingDto.setBooker(userDto);

        Mockito
                .when(bookingService.getBookingId(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", "21")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.start").value("2025-05-21T14:00:00"))
                .andExpect(jsonPath("$.end").value("2025-07-21T14:00:00"))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.item.id").value(7))
                .andExpect(jsonPath("$.item.name").value("Велосипед"))
                .andExpect(jsonPath("$.item.description").value("Очень быстрый"))
                .andExpect(jsonPath("$.item.available").value(true))
                .andExpect(jsonPath("$.item.requestId").value(55))
                .andExpect(jsonPath("$.item.ownerId").value(15))
                .andExpect(jsonPath("$.booker.id").value(21))
                .andExpect(jsonPath("$.booker.name").value("Sergey"))
                .andExpect(jsonPath("$.booker.email").value("serg007@mail.com"));

    }

    @Test
    void findAllBookingStateAllTest() throws Exception {
        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setId(1L);
        bookingDto1.setStart(LocalDateTime.of(2025, 5, 21, 14, 0));
        bookingDto1.setEnd(LocalDateTime.of(2025, 7, 21, 14, 0));
        bookingDto1.setStatus(BookingStatus.APPROVED);
        bookingDto1.setItem(new ItemDto(){{
            setId(33L); setName("Велосипед"); setDescription("Очень быстрый"); setAvailable(true); setRequestId(75L); setOwnerId(15L);
        }});
        bookingDto1.setBooker(new UserDto(){{
            setId(43L); setName("Sergey"); setEmail("serg007@mail.com");
        }});

        BookingDto bookingDto2 = new BookingDto();
        bookingDto2.setId(2L);
        bookingDto2.setStart(LocalDateTime.of(2025, 7, 23, 15, 0));
        bookingDto2.setEnd(LocalDateTime.of(2025, 7, 25, 15, 0));
        bookingDto2.setStatus(BookingStatus.WAITING);
        bookingDto2.setItem(new ItemDto(){{
            setId(99L); setName("Отвертка"); setDescription("Крестовая"); setAvailable(true); setRequestId(85L); setOwnerId(21L);
        }});
        bookingDto2.setBooker(new UserDto(){{
            setId(27L); setName("Anna"); setEmail("anna1877@mail.com");
        }});

        List<BookingDto> bookings = List.of(bookingDto1, bookingDto2);

        Mockito
                .when(bookingService.findAllBooking(anyLong(), eq(BookingState.ALL)))
                .thenReturn(bookings);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].start").value("2025-05-21T14:00:00"))
                .andExpect(jsonPath("$[0].end").value("2025-07-21T14:00:00"))
                .andExpect(jsonPath("$[0].status").value("APPROVED"))
                .andExpect(jsonPath("$[0].item.id").value(33))
                .andExpect(jsonPath("$[0].item.name").value("Велосипед"))
                .andExpect(jsonPath("$[0].booker.id").value(43))
                .andExpect(jsonPath("$[0].booker.email").value("serg007@mail.com"))

                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].start").value("2025-07-23T15:00:00"))
                .andExpect(jsonPath("$[1].end").value("2025-07-25T15:00:00"))
                .andExpect(jsonPath("$[1].status").value("WAITING"))
                .andExpect(jsonPath("$[1].item.id").value(99))
                .andExpect(jsonPath("$[1].item.description").value("Крестовая"))
                .andExpect(jsonPath("$[1].booker.id").value(27))
                .andExpect(jsonPath("$[1].booker.name").value("Anna"))

                .andExpect(jsonPath("$.size()").value(2));

    }

    @Test
    void findAllBookingStateCurrentTest() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.of(2025, 5, 21, 14, 0));
        bookingDto.setEnd(LocalDateTime.of(2025, 7, 21, 14, 0));
        bookingDto.setStatus(BookingStatus.APPROVED);
        bookingDto.setItem(new ItemDto(){{
            setId(33L); setName("Велосипед"); setDescription("Очень быстрый"); setAvailable(true); setRequestId(75L); setOwnerId(15L);
        }});
        bookingDto.setBooker(new UserDto(){{
            setId(1L); setName("Sergey"); setEmail("serg007@mail.com");
        }});

        Mockito
                .when(bookingService.findAllBooking(anyLong(), eq(BookingState.CURRENT)))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("state", "CURRENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].start").value("2025-05-21T14:00:00"))
                .andExpect(jsonPath("$[0].end").value("2025-07-21T14:00:00"))
                .andExpect(jsonPath("$[0].status").value("APPROVED"))
                .andExpect(jsonPath("$[0].item.id").value(33))
                .andExpect(jsonPath("$[0].item.name").value("Велосипед"))
                .andExpect(jsonPath("$[0].item.description").value("Очень быстрый"))
                .andExpect(jsonPath("$[0].booker.id").value(1))
                .andExpect(jsonPath("$[0].booker.name").value("Sergey"))
                .andExpect(jsonPath("$[0].booker.email").value("serg007@mail.com"));

    }

    @Test
    public void findAllBookingOwnerStateAllTest() throws Exception {
        NewBookingRequest booking = new NewBookingRequest();
        booking.setStart(LocalDateTime.of(2025, 5, 21, 14, 0));
        booking.setEnd(LocalDateTime.of(2025, 7, 21, 14, 0));
        booking.setItemId(10L);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(10L);
        itemDto.setName("Коньки");
        itemDto.setDescription("Новые");
        itemDto.setAvailable(true);
        itemDto.setOwnerId(9L);
        itemDto.setRequestId(null);

        UserDto userDto = new UserDto();
        userDto.setId(4L);
        userDto.setName("Slava");
        userDto.setEmail("slava@mail.com");

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setItem(itemDto);
        bookingDto.setBooker(userDto);
        bookingDto.setStatus(BookingStatus.APPROVED);

        List<BookingDto> bookingDtos = List.of(bookingDto);

        Mockito
                .when(bookingService.findAllBookingOwner(anyLong(), eq(BookingState.ALL)))
                .thenReturn(bookingDtos);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].item.name").value("Коньки"))
                .andExpect(jsonPath("$[0].item.description").value("Новые"))
                .andExpect(jsonPath("$[0].booker.name").value("Slava"))
                .andExpect(jsonPath("$[0].booker.email").value("slava@mail.com"));

    }

}
