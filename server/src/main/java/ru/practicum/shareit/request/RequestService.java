package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.util.List;

public interface RequestService {

    ItemRequestDto addRequest(long userId, NewItemRequestDto request);

    List<ItemRequestDto> findAllRequests();

    List<ItemRequestDto> getRequestsUserId(long userId);

    ItemRequestDto getItemRequestId(long userId, Long requestId);
}
