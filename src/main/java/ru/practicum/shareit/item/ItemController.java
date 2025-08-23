package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.CreateValidation;
import ru.practicum.shareit.exceptions.UpdateValidation;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.List;

@RestController
@RequestMapping("/items")
@Validated
@Slf4j
public class ItemController {

    private final ItemServiceImpl itemService;

    public ItemController(ItemServiceImpl itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addItem(
            @Positive @RequestHeader(name = "X-Sharer-User-Id") long userId,
            @Validated(CreateValidation.class) @RequestBody NewItemRequest request) {
        log.info("Добавляется вещь");
        request.setOwnerId(userId);
        return itemService.addItem(request);
    }

    @GetMapping
    public List<ItemDto> findAllItems(@Positive @NotNull @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Запрошен список всех вещей");
        return itemService.findAllItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemId(@Positive @NotNull @RequestHeader(name = "X-Sharer-User-Id") Long userId,@Positive @PathVariable("itemId") Long itemId) {
        log.info("Запрошена информация о вещи id={}", itemId);
        return itemService.getItemId(userId, itemId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@Validated(UpdateValidation.class) @Positive @RequestHeader(name = "X-Sharer-User-Id") Long userId,
                              @PathVariable("itemId") @Positive Long itemId, @RequestBody UpdateItemRequest request) {
        log.info("Обновляется вещь {}", itemId);
        request.setId(itemId);
        request.setOwnerId(userId);
        return itemService.updateItem(request);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader(name = "X-Sharer-User-Id") long userId, @RequestParam(name = "text") String query) {
        log.info("Запрошен поиск вещи");
        return itemService.searchItems(userId, query);
    }

}
