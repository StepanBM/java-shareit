package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.List;

public interface ItemRepository {

    List<Item> findAllItems(long userId);

    Item addItem(Item item);

    Item getItemId(Long itemId, Long userId);

    Item updateItem(UpdateItemRequest request);

    List<Item> searchItems(long userId, String query);

}
