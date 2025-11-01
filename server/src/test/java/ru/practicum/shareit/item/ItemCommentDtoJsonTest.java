package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemCommentDtoJsonTest {

    private final JacksonTester<CommentDto> jsonComDto;
    private final JacksonTester<ItemWithCommentDto> jsonItComDto;

    @Autowired
    public ItemCommentDtoJsonTest(
            JacksonTester<CommentDto> jsonComDto,
            JacksonTester<ItemWithCommentDto> jsonItComDto) {
        this.jsonComDto = jsonComDto;
        this.jsonItComDto = jsonItComDto;
    }

    @Test
    public void serializeCommentDtoJsonTest() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Супер");
        commentDto.setAuthorName("Юра");
        commentDto.setCreated(LocalDateTime.of(2025, 3, 30, 14, 0));

        JsonContent<CommentDto> json = jsonComDto.write(commentDto);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.text").isEqualTo("Супер");
        assertThat(json).extractingJsonPathStringValue("$.authorName").isEqualTo("Юра");
        assertThat(json).extractingJsonPathStringValue("$.created").isEqualTo("2025-03-30T14:00:00");
    }

    @Test
    public void deserializeCommentDtoJsonTest() throws Exception {
        String json = """
              {
              "id": 1,
              "text": "Класс",
              "authorName": "Варя",
              "created": "2025-04-03T19:00:00"
              }
            """;

        CommentDto dto = jsonComDto.parse(json).getObject();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getText()).isEqualTo("Класс");
        assertThat(dto.getAuthorName()).isEqualTo("Варя");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2025, 4, 3, 19, 0));
    }

    @Test
    public void serializeItemWithCommentDtoJsonTest() throws Exception {

        CommentDto commentDto = new CommentDto();
        commentDto.setId(7L);
        commentDto.setText("Удобная, теплая");
        commentDto.setAuthorName("Андрей");
        commentDto.setCreated(LocalDateTime.of(2025, 9, 15, 12, 0, 0));

        ItemWithCommentDto dto = new ItemWithCommentDto();
        dto.setId(3L);
        dto.setName("Шуба");
        dto.setDescription("Норковая");
        dto.setAvailable(true);
        dto.setRequest(5L);
        dto.setComments(List.of(commentDto));
        dto.setLastBooking(LocalDateTime.of(2025, 10, 21, 17, 0));
        dto.setNextBooking(LocalDateTime.of(2025, 11, 9, 10, 0));

        JsonContent<ItemWithCommentDto> json = jsonItComDto.write(dto);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(3);
        assertThat(json).extractingJsonPathStringValue("$.name").isEqualTo("Шуба");
        assertThat(json).extractingJsonPathStringValue("$.description").isEqualTo("Норковая");
        assertThat(json).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(json).extractingJsonPathNumberValue("$.request").isEqualTo(5);

        assertThat(json).extractingJsonPathArrayValue("$.comments").hasSize(1);
        assertThat(json).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo("Андрей");
        assertThat(json).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("Удобная, теплая");
        assertThat(json).extractingJsonPathStringValue("$.comments[0].created").isEqualTo("2025-09-15T12:00:00");
        assertThat(json).extractingJsonPathStringValue("$.lastBooking").isEqualTo("2025-10-21T17:00:00");
        assertThat(json).extractingJsonPathStringValue("$.nextBooking").isEqualTo("2025-11-09T10:00:00");
    }

    @Test
    public void deserializeItemWithCommentDtoJsonTest() throws Exception {
        String json = """
              {
              "id": 25,
              "name": "Зарядка",
              "description": "Быстрая",
              "available": true,
              "request": 456,
              "comments": [
                {
                  "id": 99,
                  "text": "Практичная",
                  "authorName": "Женя",
                  "created": "2025-10-07T09:00:00"
                }
              ],
              "lastBooking": "2025-08-10T15:00:00",
              "nextBooking": "2025-10-03T12:00:00"
            }
            """;

        ItemWithCommentDto dto = jsonItComDto.parse(json).getObject();

        assertThat(dto.getId()).isEqualTo(25L);
        assertThat(dto.getName()).isEqualTo("Зарядка");
        assertThat(dto.getComments()).hasSize(1);
        CommentDto comment = dto.getComments().get(0);
        assertThat(comment.getText()).isEqualTo("Практичная");
        assertThat(comment.getAuthorName()).isEqualTo("Женя");
        assertThat(comment.getCreated()).isEqualTo(LocalDateTime.of(2025, 10, 7, 9, 0));
        assertThat(dto.getLastBooking()).isEqualTo(LocalDateTime.of(2025, 8, 10, 15, 0));
        assertThat(dto.getNextBooking()).isEqualTo(LocalDateTime.of(2025, 10, 3, 12, 0));
    }

}
