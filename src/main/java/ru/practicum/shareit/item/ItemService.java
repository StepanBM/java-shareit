package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.List;

public interface ItemService {

    List<ItemDto> findAllItems(long userId);

    ItemDto addItem(NewItemRequest request);

    ItemDto getItemId(Long userId, Long itemId);

    ItemDto updateItem(UpdateItemRequest request);

    List<ItemDto> searchItems(long userId, String query);
}
