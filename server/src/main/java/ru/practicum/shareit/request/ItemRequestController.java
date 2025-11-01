package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.CreateValidation;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Validated
@Slf4j
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final RequestService requestService;

    public ItemRequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ItemRequestDto addRequest(
            @RequestHeader(name = "X-Sharer-User-Id") long userId,
            @Validated(CreateValidation.class) @RequestBody NewItemRequestDto request) {
       // log.info("Добавляется запрос");
        request.setCreated(LocalDateTime.now());
        return requestService.addRequest(userId, request);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAllRequests() {
       // log.info("Вывод всех запросов");
        return requestService.findAllRequests();
    }

    @GetMapping
    public List<ItemRequestDto> getRequestsUserId(@RequestHeader(name = "X-Sharer-User-Id") long userId) {
       // log.info("Получение запросов своих запросов");
        return requestService.getRequestsUserId(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestId(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                           @PathVariable long requestId) {
       // log.info("Получение данных о конкретном запросе");
        return requestService.getItemRequestId(userId, requestId);
    }
}
