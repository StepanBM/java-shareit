package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("itemRepository")
public class ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();

    public List<Item> findAllItems(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .toList();
    }

    public Item addItem(Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);
        return item;
    }

    public Item getItemId(Long itemId, Long userId) {
        Item item = items.get(itemId);
        if (item == null) {
            throw new NotFoundException("Вещь с id=" + itemId + " не найден");
        }
        if (item.getOwner().getId() != userId) {
            throw new NotFoundException("Вещь с id=" + itemId + " не является вещью пользователя с id=" + userId);
        }
        return item;
    }

    public Item updateItem(UpdateItemRequest request) {
        Item item = items.get(request.getId());
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setAvailable(request.getAvailable());

        return item;
    }

    public List<Item> searchItems(long userId, String query) {
        if (query != null && !query.isEmpty()) {
            return items.values().stream()
                    .filter(item -> item.getOwner().getId() == userId)
                    .filter(item -> item.getAvailable().equals(Boolean.TRUE))
                    .filter(item -> item.getName().toLowerCase().contains(query.toLowerCase()) || item.getDescription().toLowerCase().contains(query.toLowerCase()))
                    .toList();
        }
        return Collections.emptyList();
    }

    // Вспомогательный метод для генерации идентификатора
    private long getNextId() {
        long currentMaxId = items.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
