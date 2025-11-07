package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemMapper {
    public static Item mapToItem(NewItemRequest request) {
        Item item = new Item();
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setAvailable(request.getAvailable());

        return item;
    }

    public static ItemDto mapToItemDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());

        if (item.getRequest() != null) {
            dto.setRequestId(item.getRequest().getId());
        }

        if (item.getOwner() != null) {
            dto.setOwnerId(item.getOwner().getId());
        }

        return dto;
    }

    public static ItemWithCommentDto mapToItemCommentDto(Item item, List<CommentDto> comments,
                                                         LocalDateTime lastBooking, LocalDateTime nextBooking) {
        ItemWithCommentDto itemCommentDto = new ItemWithCommentDto();
        itemCommentDto.setId(item.getId());
        itemCommentDto.setName(item.getName());
        itemCommentDto.setDescription(item.getDescription());
        itemCommentDto.setAvailable(item.getAvailable());

        if (item.getRequest() != null) {
            itemCommentDto.setRequest(item.getRequest().getId());
        }

        itemCommentDto.setComments(comments);
        itemCommentDto.setLastBooking(lastBooking);
        itemCommentDto.setNextBooking(nextBooking);

        return itemCommentDto;
    }

    public static Item updateItemFields(Item item, UpdateItemRequest request) {

        if (request.hasName()) {
            item.setName(request.getName());
        }
        if (request.hasDescription()) {
            item.setDescription(request.getDescription());
        }
        if (request.hasAvailable()) {
            item.setAvailable(request.getAvailable());
        }

        return item;
    }
}
