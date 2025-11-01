package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemService itemService;

    // Создание вещи
    @Test
    public void addItemTest() throws Exception {

        NewItemRequest request = new NewItemRequest();
        request.setName("Наушники");
        request.setDescription("Описание наушников");
        request.setAvailable(true);
        request.setOwnerId(1L);
        request.setRequestId(2L);

        ItemDto item1 = new ItemDto();
        item1.setId(1L);
        item1.setName("Наушники");
        item1.setDescription("Описание наушников");
        item1.setAvailable(true);
        item1.setOwnerId(1L);
        item1.setRequestId(2L);

        Mockito
                .when(itemService.addItem(request)).thenReturn(item1);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Наушники"))
                .andExpect(jsonPath("$.description").value("Описание наушников"));

    }

    // Получение всех вещей
    @Test
    public void findAllItemsTest() throws Exception {
        ItemWithCommentDto itemWithCommentDto = new ItemWithCommentDto();
        itemWithCommentDto.setId(1L);
        itemWithCommentDto.setName("Пылесос");
        itemWithCommentDto.setDescription("Хорошее описание");
        itemWithCommentDto.setAvailable(true);
        itemWithCommentDto.setRequest(2L);
        itemWithCommentDto.setLastBooking(null);
        itemWithCommentDto.setNextBooking(null);

        CommentDto commentDto = new CommentDto();
        commentDto.setId(1);
        commentDto.setText("Длинный текст");
        commentDto.setAuthorName("Sergey");
        commentDto.setCreated(LocalDateTime.now());

        itemWithCommentDto.setComments(List.of(commentDto));

        ItemWithCommentDto itemWithCommentDto2 = new ItemWithCommentDto();
        itemWithCommentDto2.setId(2L);
        itemWithCommentDto2.setName("Чайник");
        itemWithCommentDto2.setDescription("Отличное описание");
        itemWithCommentDto2.setAvailable(true);
        itemWithCommentDto2.setRequest(3L);
        itemWithCommentDto2.setLastBooking(null);
        itemWithCommentDto2.setNextBooking(null);

        CommentDto commentDto2 = new CommentDto();
        commentDto2.setId(2);
        commentDto2.setText("Важный текст");
        commentDto2.setAuthorName("Sveta");
        commentDto2.setCreated(LocalDateTime.now());

        itemWithCommentDto2.setComments(List.of(commentDto2));

        List<ItemWithCommentDto> items = List.of(itemWithCommentDto, itemWithCommentDto2);

        Mockito
                .when(itemService.findAllItems(1L)).thenReturn(items);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Пылесос"))
                .andExpect(jsonPath("$[0].description").value("Хорошее описание"))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[0].request").value(2))
                .andExpect(jsonPath("$[0].lastBooking").doesNotExist())
                .andExpect(jsonPath("$[0].nextBooking").doesNotExist())
                .andExpect(jsonPath("$[0].comments[0].id").value(1))
                .andExpect(jsonPath("$[0].comments[0].text").value("Длинный текст"))
                .andExpect(jsonPath("$[0].comments[0].authorName").value("Sergey"))
                .andExpect(jsonPath("$[0].comments[0].created").exists())

                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Чайник"))
                .andExpect(jsonPath("$[1].description").value("Отличное описание"))
                .andExpect(jsonPath("$[1].available").value(true))
                .andExpect(jsonPath("$[1].request").value(3))
                .andExpect(jsonPath("$[1].comments[0].id").value(2))
                .andExpect(jsonPath("$[1].comments[0].text").value("Важный текст"))
                .andExpect(jsonPath("$[1].comments[0].authorName").value("Sveta"))

                .andExpect(jsonPath("$.size()").value(2));

    }

    // Получение вещи по id
    @Test
    public void getItemIdTest() throws Exception {

        CommentDto commentDto = new CommentDto();
        commentDto.setId(1);
        commentDto.setText("О чём-то текст");
        commentDto.setAuthorName("Anna");
        commentDto.setCreated(LocalDateTime.of(2025, 11, 2, 15, 0));

        ItemWithCommentDto itemDto = new ItemWithCommentDto();
        itemDto.setId(1L);
        itemDto.setName("Самовар");
        itemDto.setDescription("Классное описание");
        itemDto.setAvailable(true);
        itemDto.setRequest(3L);
        itemDto.setComments(List.of(commentDto));
        itemDto.setLastBooking(LocalDateTime.of(2025, 9, 30, 10, 0));
        itemDto.setNextBooking(LocalDateTime.of(2025, 11, 3, 17, 0));

        Mockito
                .when(itemService.getItemId(1L, 1L)).thenReturn(itemDto);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Самовар"))
                .andExpect(jsonPath("$.description").value("Классное описание"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.request").value(3))
                .andExpect(jsonPath("$.comments[0].id").value(1))
                .andExpect(jsonPath("$.comments[0].text").value("О чём-то текст"))
                .andExpect(jsonPath("$.comments[0].authorName").value("Anna"))
                .andExpect(jsonPath("$.comments[0].created").value("2025-11-02T15:00:00"));
    }

    // Обновление вещи
    @Test
    public void updateItemTest() throws Exception {
        UpdateItemRequest request = new UpdateItemRequest();
        request.setId(1L);
        request.setName("Чашка");
        request.setDescription("Полезное описание");
        request.setAvailable(false);
        request.setOwnerId(3L);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Чашка");
        itemDto.setDescription("Полезное описание");
        itemDto.setAvailable(false);
        itemDto.setRequestId(5L);
        itemDto.setOwnerId(3L);

        Mockito
                .when(itemService.updateItem(request)).thenReturn(itemDto);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", "3")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Чашка"))
                .andExpect(jsonPath("$.description").value("Полезное описание"))
                .andExpect(jsonPath("$.available").value(false))
                .andExpect(jsonPath("$.requestId").value(5))
                .andExpect(jsonPath("$.ownerId").value(3));
    }

    // Поиск вещи
    @Test
    void searchItemsTest() throws Exception {

        String query = "выключатель";

        ItemDto item1 = new ItemDto();
        item1.setId(1L);
        item1.setName("Выключатель одинарный");
        item1.setDescription("Описание №1");
        item1.setAvailable(true);
        item1.setOwnerId(1L);
        item1.setRequestId(2L);

        ItemDto item2 = new ItemDto();
        item2.setId(2L);
        item2.setName("Выключатель двойной");
        item2.setDescription("Описание №2");
        item2.setAvailable(true);
        item2.setOwnerId(1L);
        item2.setRequestId(2L);

        List<ItemDto> items = Arrays.asList(item1, item2);

        Mockito
                .when(itemService.searchItems(1L,"выключатель")).thenReturn(items);

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", query)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].id").value(1))
                .andExpect(jsonPath("[0].name").value("Выключатель одинарный"))
                .andExpect(jsonPath("[1].id").value(2))
                .andExpect(jsonPath("[1].name").value("Выключатель двойной"));
    }

    // Создание комментария
    @Test
    public void addCommentTest() throws Exception {
        NewCommentRequest request = new NewCommentRequest();
        request.setUserId(7L);
        request.setItemId(33L);
        request.setText("Полезный комментарий");

        CommentDto commentDto = new CommentDto();
        commentDto.setId(21L);
        commentDto.setText("Полезный комментарий");
        commentDto.setAuthorName("Slava");
        commentDto.setCreated(LocalDateTime.of(2025, 7, 3, 12, 10));

        Mockito
                .when(itemService.addComment(7L, request,33L))
                .thenReturn(commentDto);

        mvc.perform(post("/items/33/comment")
                        .header("X-Sharer-User-Id", "7")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(21))
                .andExpect(jsonPath("$.text").value("Полезный комментарий"))
                .andExpect(jsonPath("$.authorName").value("Slava"))
                .andExpect(jsonPath("$.created").value("2025-07-03T12:10:00"));
    }

}
