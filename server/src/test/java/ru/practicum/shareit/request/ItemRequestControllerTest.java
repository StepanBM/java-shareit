package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RequestService requestService;

    @Test
    public void addRequestTest() throws Exception {

        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setOwnerId(1L);
        item.setName("Куртка");

        NewItemRequestDto request = new NewItemRequestDto();
        request.setDescription("Нужна куртка");

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Нужна куртка");
        itemRequestDto.setCreated(LocalDateTime.now());
        itemRequestDto.setItems(List.of(item));

        Mockito
                .when(requestService.addRequest(anyLong(), any(NewItemRequestDto.class))).thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Нужна куртка"));
    }

    @Test
    public void addRequestUserNotFoundError() throws Exception {

        NewItemRequestDto request = new NewItemRequestDto();
        request.setDescription("Нужна куртка");

        Mockito
                .when(requestService.addRequest(eq(999L), any(NewItemRequestDto.class)))
                .thenThrow(new NotFoundException("Пользователь с id " + 999L + " не найден"));

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void findAllRequestsTest() throws Exception {

        ItemRequestDto request1 = new ItemRequestDto();
        request1.setId(1L);
        request1.setDescription("Нужен чайник");
        request1.setCreated(LocalDateTime.of(2023, 10, 10, 12, 0));
        request1.setItems(Collections.emptyList());

        ItemRequestDto request2 = new ItemRequestDto();
        request2.setId(2L);
        request2.setDescription("Нужен магнитофон");
        request2.setCreated(LocalDateTime.of(2023, 10, 11, 13, 0));
        request2.setItems(Collections.emptyList());

        List<ItemRequestDto> mockRequests = List.of(request1, request2);

        Mockito
                .when(requestService.findAllRequests()).thenReturn(mockRequests);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Нужен чайник"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].description").value("Нужен магнитофон"))
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    public void getRequestsUserIdTest() throws Exception {

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Необходим плеер");
        itemRequestDto.setCreated(LocalDateTime.now());

        Mockito
                .when(requestService.getRequestsUserId(anyLong()))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Необходим плеер"));
    }

    @Test
    public void getItemRequestIdTest() throws Exception {

        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(3L);
        dto.setDescription("Скоро понадодится гитара");
        dto.setCreated(LocalDateTime.now());

        Mockito
                .when(requestService.getItemRequestId(anyLong(), anyLong()))
                .thenReturn(dto);

        mvc.perform(get("/requests/3")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.description").value("Скоро понадодится гитара"));
    }


    @Test
    public void getRequestsUserIdUserNotFoundError() throws Exception {

        Mockito
                .when(requestService.getRequestsUserId(999L))
                .thenThrow(new NotFoundException("Пользователь с id " + 999L + " не найден"));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getRequestsUserIdNoRequestsError() throws Exception {

        Mockito
                .when(requestService.getRequestsUserId(1L)).thenReturn(Collections.emptyList());

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    public void getItemRequestIdRequestNotFoundError404() throws Exception {

        Mockito
                .when(requestService.getItemRequestId(1L, 999L))
                .thenThrow(new NotFoundException("Данный запрос не найден"));

        mvc.perform(get("/requests/999")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getItemRequestIdUserNotFoundError404() throws Exception {

        Mockito
                .when(requestService.getItemRequestId(999L, 1L))
                .thenThrow(new NotFoundException("Данный пользователь не найден"));

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", "999"))
                .andExpect(status().isNotFound());
    }

}
