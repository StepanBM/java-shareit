package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemClient itemClient;

    @Test
    void addItemEmptyName() throws Exception {
        NewItemRequest request = new NewItemRequest();
        request.setName("");
        request.setDescription("Описание наушников");
        request.setAvailable(true);
        request.setOwnerId(1L);
        request.setRequestId(2L);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addItemEmptyDescription() throws Exception {
        NewItemRequest request = new NewItemRequest();
        request.setName("Alex");
        request.setDescription("");
        request.setAvailable(true);
        request.setOwnerId(1L);
        request.setRequestId(2L);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addItemEmptyAvailable() throws Exception {
        NewItemRequest request = new NewItemRequest();
        request.setName("Alex");
        request.setDescription("Описание наушников");
        request.setAvailable(null);
        request.setOwnerId(1L);
        request.setRequestId(2L);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addItemErrorHeader() throws Exception {
        NewItemRequest request = new NewItemRequest();
        request.setName("Alex");
        request.setDescription("Описание наушников");
        request.setAvailable(true);
        request.setOwnerId(1L);
        request.setRequestId(2L);

        mvc.perform(post("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addItemNameErrorLength() throws Exception {
        NewItemRequest request = new NewItemRequest();
        request.setName("A".repeat(256));
        request.setDescription("Описание наушников");
        request.setAvailable(true);
        request.setOwnerId(1L);
        request.setRequestId(2L);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addItemDescriptionErrorLength() throws Exception {
        NewItemRequest request = new NewItemRequest();
        request.setName("Alex");
        request.setDescription("О".repeat(513));
        request.setAvailable(true);
        request.setOwnerId(1L);
        request.setRequestId(2L);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAllItemsErrorHeader() throws Exception {

        mvc.perform(get("/items")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItemIdErrorHeader() throws Exception {

        mvc.perform(get("/items/1")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItemIdErrorId() throws Exception {

        mvc.perform(get("/items/ZZZ")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateItemErrorHeader() throws Exception {

        UpdateItemRequest request = new UpdateItemRequest();
        request.setId(1L);
        request.setName("Чашка");
        request.setDescription("Полезное описание");
        request.setAvailable(false);

        mvc.perform(patch("/items/1")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateItemErrorName() throws Exception {

        UpdateItemRequest request = new UpdateItemRequest();
        request.setId(1L);
        request.setName("");
        request.setDescription("Полезное описание");
        request.setAvailable(false);

        mvc.perform(patch("/items/1")
                .header("X-Sharer-User-Id", "3")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateItemErrorNameLength() throws Exception {

        UpdateItemRequest request = new UpdateItemRequest();
        request.setId(1L);
        request.setName("A".repeat(256));
        request.setDescription("Полезное описание");
        request.setAvailable(false);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", "3")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateItemErrorDescription() throws Exception {

        UpdateItemRequest request = new UpdateItemRequest();
        request.setId(1L);
        request.setName("Чашка");
        request.setDescription("");
        request.setAvailable(false);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", "3")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateItemErrorDescriptionLength() throws Exception {

        UpdateItemRequest request = new UpdateItemRequest();
        request.setId(1L);
        request.setName("Чашка");
        request.setDescription("О".repeat(513));
        request.setAvailable(false);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", "3")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateItemErrorAvailable() throws Exception {

        UpdateItemRequest request = new UpdateItemRequest();
        request.setId(1L);
        request.setName("Чашка");
        request.setDescription("Полезное описание");
        request.setAvailable(null);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", "3")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void searchItemsErrorParamSearch() throws Exception {
        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void searchItemsErrorHeader() throws Exception {
        mvc.perform(get("/items/search")
                        .param("text", "")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addCommentErrorHeader() throws Exception {
        NewCommentRequest request = new NewCommentRequest();
        request.setText("Comment");

        mvc.perform(post("/items/1/comment")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addCommentErrorId() throws Exception {
        NewCommentRequest request = new NewCommentRequest();
        request.setText("Comment");

        mvc.perform(post("/items/ZZZ/comment")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addCommentTextBlank() throws Exception {
        NewCommentRequest request = new NewCommentRequest();
        request.setText("");

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addCommentTextNull() throws Exception {
        NewCommentRequest request = new NewCommentRequest();
        request.setText(null);

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

}




