package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.CreateValidation;
import ru.practicum.shareit.exceptions.UpdateValidation;
import ru.practicum.shareit.item.dto.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@Validated
@Slf4j
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addItem(
             @RequestHeader(name = "X-Sharer-User-Id") long userId,
            @Validated(CreateValidation.class) @RequestBody NewItemRequest request) {
       // log.info("Добавляется вещь");
        request.setOwnerId(userId);
        return itemService.addItem(request);
    }

    @GetMapping
    public List<ItemWithCommentDto> findAllItems(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
       // log.info("Запрошен список всех вещей");
        return itemService.findAllItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemWithCommentDto getItemId( @RequestHeader(name = "X-Sharer-User-Id") Long userId, @PathVariable("itemId") Long itemId) {
       // log.info("Запрошена информация о вещи id={}", itemId);
        return itemService.getItemId(userId, itemId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@Validated(UpdateValidation.class) @RequestHeader(name = "X-Sharer-User-Id") Long userId,
                              @PathVariable("itemId") Long itemId, @RequestBody UpdateItemRequest request) {
       // log.info("Обновляется вещь {}", itemId);
        request.setId(itemId);
        request.setOwnerId(userId);
        return itemService.updateItem(request);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader(name = "X-Sharer-User-Id") long userId, @RequestParam(name = "text") String query) {
       // log.info("Запрошен поиск вещи");
        return itemService.searchItems(userId, query);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                 @Validated(UpdateValidation.class) @RequestBody NewCommentRequest request,
                                  @PathVariable("itemId") long itemId) {
       // log.info("Запрос на добавления комментария");
        return itemService.addComment(userId, request, itemId);
    }

}
