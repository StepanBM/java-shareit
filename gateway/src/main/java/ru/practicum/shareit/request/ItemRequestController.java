package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.time.LocalDateTime;

@RestController
@Validated
@Slf4j
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient requestClient;

    public ItemRequestController(ItemRequestClient requestClient) {
        this.requestClient = requestClient;
    }

    @PostMapping
    public ResponseEntity<Object> addRequest(@Positive @NotNull @RequestHeader(name = "X-Sharer-User-Id") long userId,
                                                    @RequestBody @Valid NewItemRequestDto request) {
       // log.info("Добавляется запрос");
        request.setCreated(LocalDateTime.now());
        return requestClient.addRequest(userId, request);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllRequests() {
       // log.info("Вывод всех запросов");
        return requestClient.findAllRequests();

    }

    @GetMapping
    public ResponseEntity<Object> getRequestsUserId(@NotNull @RequestHeader(name = "X-Sharer-User-Id") long userId) {
       // log.info("Получение запросов своих запросов");
        return requestClient.getRequestsUserId(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestId(@NotNull @RequestHeader(name = "X-Sharer-User-Id") long userId,
                                                 @NotNull @PathVariable long requestId) {
       //log.info("Получение данных о конкретном запросе");
        return requestClient.getItemRequestId(userId, requestId);
    }
}
