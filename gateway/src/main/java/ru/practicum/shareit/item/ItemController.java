package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.validation.CreateValidation;
import ru.practicum.shareit.validation.UpdateValidation;

import java.util.Collections;


@RestController
@Validated
@Slf4j
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;

    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @PostMapping
    public ResponseEntity<Object> addItem(@Positive @NotNull @RequestHeader(name = "X-Sharer-User-Id") long userId,
                                          @Validated(CreateValidation.class) @RequestBody NewItemRequest request) {
        log.info("Добавляется вещь");
        return itemClient.addItem(userId, request);
    }

    @GetMapping()
    public ResponseEntity<Object> findAllItems(@Positive @NotNull @RequestHeader(name = "X-Sharer-User-Id") long userId) {
        log.info("Запрошен список всех вещей");
        return itemClient.findAllItems(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemId(@Positive @NotNull @RequestHeader(name = "X-Sharer-User-Id") long userId,
                                            @Positive @NotNull @PathVariable("itemId") long itemId) {
        log.info("Запрошена информация о вещи id={}", itemId);
        return itemClient.getItemId(userId, itemId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                             @Positive @NotNull @PathVariable("itemId") long itemId,
                                             @Validated(UpdateValidation.class) @RequestBody UpdateItemRequest request) {
        log.info("Обновляется вещь {}", itemId);
        request.setId(itemId);
        request.setOwnerId(userId);
        return itemClient.updateItem(userId, itemId, request);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@Positive @NotNull @RequestHeader(name = "X-Sharer-User-Id") long userId,
                                              @RequestParam(name = "text") String query) {
        log.info("Запрошен поиск вещи");
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return itemClient.searchItems(userId, query);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@Positive @NotNull @RequestHeader(name = "X-Sharer-User-Id") long userId,
                                             @Validated(CreateValidation.class) @RequestBody NewCommentRequest request,
                                             @Positive @NotNull @PathVariable("itemId") long itemId) {
        log.info("Запрос на добавления комментария");
        return itemClient.addComment(userId, request, itemId);
    }
}
