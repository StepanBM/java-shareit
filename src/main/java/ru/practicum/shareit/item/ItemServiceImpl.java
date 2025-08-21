package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.user.UserRepositoryImpl;
import ru.practicum.shareit.user.User;

import java.util.List;

import static ru.practicum.shareit.item.ItemMapper.mapToItem;
import static ru.practicum.shareit.item.ItemMapper.mapToItemDto;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepositoryImpl itemRepository;
    private final UserRepositoryImpl userRepository;

    @Autowired
    public ItemServiceImpl(@Qualifier("itemRepository") ItemRepositoryImpl itemRepository,
                           UserRepositoryImpl userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Qualifier
    public List<ItemDto> findAllItems(long userId) {
        userRepository.getUserId(userId);
        return itemRepository.findAllItems(userId).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Qualifier
    public ItemDto addItem(NewItemRequest request) {
        log.debug("Начинается добавление вещи по запросу {}", request);
        User user = userRepository.getUserId(request.getOwnerId());
        Item item = mapToItem(request);
        item.setOwner(user);
        log.debug("Запрос на добавление вещи конвертирован в объект класса Item {}", item);
        item = itemRepository.addItem(item);
        log.debug("Добавлена вещь {}", item);
        return mapToItemDto(item);
    }

    @Qualifier
    public ItemDto getItemId(Long userId, Long itemId) {
        userRepository.getUserId(userId);
        return mapToItemDto(itemRepository.getItemId(itemId, userId));
    }

    @Qualifier
    public ItemDto updateItem(UpdateItemRequest request) {
        itemRepository.getItemId(request.getId(), request.getOwnerId());
        log.debug("Начинается обновление вещи по запросу {}", request);
        Item updatedItem = itemRepository.updateItem(request);
        log.debug("Обновлена вещь {}", updatedItem);
        return mapToItemDto(updatedItem);
    }

    @Qualifier
    public List<ItemDto> searchItems(long userId, String query) {
        userRepository.getUserId(userId);
        log.debug("Начинается поиск вещи по запросу {}", query);
        return itemRepository.searchItems(userId, query).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

}
