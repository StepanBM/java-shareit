package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.NewBookingRequest;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingClient bookingClient;

    @Test
    void addBookingErrorStartTime() throws Exception {
        NewBookingRequest request = new NewBookingRequest();
        request.setStart(LocalDateTime.of(1999, 5, 21, 14, 0));
        request.setEnd(LocalDateTime.of(2026, 7, 21, 14, 0));
        request.setItemId(7L);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addBookingErrorEndTime() throws Exception {
        NewBookingRequest request = new NewBookingRequest();
        request.setStart(LocalDateTime.of(2026, 5, 21, 14, 0));
        request.setEnd(LocalDateTime.of(1999, 5, 21, 14, 0));
        request.setItemId(7L);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addBookingErrorItemId() throws Exception {
        NewBookingRequest request = new NewBookingRequest();
        request.setStart(LocalDateTime.of(2025, 5, 21, 14, 0));
        request.setEnd(LocalDateTime.of(2025, 7, 21, 14, 0));
        request.setItemId(null);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addBookingErrorHeader() throws Exception {
        NewBookingRequest request = new NewBookingRequest();
        request.setStart(LocalDateTime.of(2026, 5, 21, 14, 0));
        request.setEnd(LocalDateTime.of(2026, 7, 21, 14, 0));
        request.setItemId(7L);

        mvc.perform(post("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateBookingStatusErrorApproved() throws Exception {

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("approved", "ZZZZ"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateBookingStatusErrorHeader() throws Exception {

        mvc.perform(patch("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("approved", "true"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateBookingStatusErrorId() throws Exception {

        mvc.perform(patch("/bookings/ZZZ")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("approved", "ZZZZ"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingError() throws Exception {

        mvc.perform(get("/bookings/ZZZZ")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingErrorHeader() throws Exception {

        mvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAllBookingErrorHeader() throws Exception {

        mvc.perform(get("/bookingsZZZ")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("state", "ALL"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllBookingError() throws Exception {

        mvc.perform(get("/bookingsZZZ")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("state", "ALL"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllBookingOwnerErrorHeader() throws Exception {

        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("state", "ALL"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAllBookingOwnerError() throws Exception {

        mvc.perform(get("/bookings/ownerZZZZ")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("state", "ALL"))
                .andExpect(status().isBadRequest());
    }

}
