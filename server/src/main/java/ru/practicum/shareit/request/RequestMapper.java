package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RequestMapper {

    public static ItemRequest mapToRequest(NewItemRequestDto requestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(requestDto.getDescription());
        itemRequest.setCreated(LocalDateTime.now());

        return itemRequest;
    }

    public static ItemRequestDto mapToRequestDto(ItemRequest itemRequest) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(itemRequest.getId());
        dto.setDescription(itemRequest.getDescription());
        dto.setCreated(itemRequest.getCreated());

        if (itemRequest.getItems() != null) {
            List<ItemDto> itemDtos = itemRequest.getItems().stream()
                    .map(item -> {
                        ItemDto itemDto = new ItemDto();
                        itemDto.setId(item.getId());
                        if (item.getOwner() != null) {
                            itemDto.setOwnerId(item.getOwner().getId());
                        }
                        itemDto.setName(item.getName());
                        return itemDto;
                    })
                    .collect(Collectors.toList());
            dto.setItems(itemDtos);
        }

        return dto;
    }

//    public static ItemDto mapToItemDto(Item item) {
//        ItemDto dto = new ItemDto();
//        dto.setId(item.getId());
//        dto.setName(item.getName());
//        dto.setDescription(item.getDescription());
//        dto.setAvailable(item.getAvailable());
//        if (item.getRequest() != null) {
//            dto.setRequestId(item.getRequest().getId());
//        }
//        if (item.getOwner() != null) {
//            dto.setOwnerId(item.getOwner().getId());
//        }
//        return dto;
//    }
}
