package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {

    List<ItemWithCommentDto> findAllItems(long userId);

    ItemDto addItem(NewItemRequest request);

    ItemWithCommentDto getItemId(Long userId, Long itemId);

    ItemDto updateItem(UpdateItemRequest request);

    List<ItemDto> searchItems(long userId, String query);

    CommentDto addComment(long userId, NewCommentRequest request, long itemId);
}
