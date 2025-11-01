package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JsonTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemRequestDtoJsonTest {

    private final JacksonTester<ItemRequestDto> jsonReqDto;
    private final JacksonTester<NewItemRequestDto> jsonNewReqDto;

    @Test
    public void serializeItemRequestDtoJsonTest() throws Exception {

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Ищу сапоги");
        itemRequestDto.setCreated(LocalDateTime.of(2025, 4, 21, 15, 0));
        itemRequestDto.setItems(List.of());

        JsonContent<ItemRequestDto> json = jsonReqDto.write(itemRequestDto);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.description").isEqualTo("Ищу сапоги");
        assertThat(json).extractingJsonPathStringValue("$.created").isEqualTo("2025-04-21T15:00:00");
        assertThat(json).extractingJsonPathArrayValue("$.items").isEmpty();
    }

    @Test
    public void deserializeJsonToItemRequestDtoJsonTest() throws Exception {
        String json = """
                {
                  "id": 1,
                  "description": "Нужен цветочный горшок",
                  "created": "2025-04-21T15:00:00",
                  "items": [
                    {
                      "id": 3,
                      "name": "Цветочный горшок",
                      "description": "Красивый и надежный",
                      "available": true,
                      "requestId": 1,
                      "ownerId": 4
                    }
                  ]
                }
                """;

        ItemRequestDto itemRequestDto = jsonReqDto.parse(json).getObject();

        assertEquals(1L, itemRequestDto.getId());
        assertEquals("Нужен цветочный горшок", itemRequestDto.getDescription());
        assertEquals(LocalDateTime.of(2025, 4, 21, 15, 0), itemRequestDto.getCreated());
        assertEquals(1, itemRequestDto.getItems().size());

        ItemDto itemDto = itemRequestDto.getItems().get(0);
        assertEquals("Цветочный горшок", itemDto.getName());
        assertEquals("Красивый и надежный", itemDto.getDescription());
        assertTrue(itemDto.getAvailable());
        assertEquals(1L, itemDto.getRequestId());
        assertEquals(4L, itemDto.getOwnerId());
    }

    @Test
    public void serializeNewItemRequestDtoJsonTest() throws Exception {
        NewItemRequestDto newRequest = new NewItemRequestDto();
        newRequest.setDescription("Нужны туфли");
        newRequest.setCreated(LocalDateTime.of(2025, 4, 21, 15, 0));

        JsonContent<NewItemRequestDto> json = jsonNewReqDto.write(newRequest);

        assertThat(json).extractingJsonPathStringValue("$.description").isEqualTo("Нужны туфли");
        assertThat(json).extractingJsonPathStringValue("$.created").isEqualTo("2025-04-21T15:00:00");
    }

    @Test
    public void deserializeJsonToNewItemRequestDtoJsonTest() throws Exception {
        String json = """
                {
                  "requestId": 5,
                  "description": "Срочно нужен паяльник",
                  "created": "2025-04-21T15:00:00"
                }
                """;

        NewItemRequestDto newItemRequestDto = jsonNewReqDto.parse(json).getObject();

        assertEquals(5L, newItemRequestDto.getRequestId());
        assertEquals("Срочно нужен паяльник", newItemRequestDto.getDescription());
        assertEquals(LocalDateTime.of(2025, 4, 21, 15, 0), newItemRequestDto.getCreated());
    }
}
