package ru.practicum.shareit.request;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.request.RequestMapper.mapToRequest;


@Qualifier("ItemRequestDbService")
@Service
@Slf4j
public class ItemRequestServiceImpl implements RequestService {


    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;


    @Autowired
    public ItemRequestServiceImpl(ItemRepository itemRepository,
                                  UserRepository userRepository,
                                  RequestRepository requestRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;

    }

    @Override
    @Transactional
    public ItemRequestDto addRequest(long userId, NewItemRequestDto request) {
       // log.debug("Начинается добавление запроса {}", request);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        ItemRequest itemRequest = mapToRequest(request);
        itemRequest.setRequestor(user);
        itemRequest = requestRepository.save(itemRequest);
       // log.debug("Запрос на добавление вещи конвертирован в объект класса Item {}", itemRequest);
        return RequestMapper.mapToRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> findAllRequests() {
        return requestRepository.findAll().stream()
                .map(RequestMapper::mapToRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getRequestsUserId(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        return requestRepository.findAllByRequestor(user).stream()
                .map(RequestMapper::mapToRequestDto)
                .collect(Collectors.toList());
    }
    @Override
    public ItemRequestDto getItemRequestId(long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request с id " + requestId + " не найден"));
        return RequestMapper.mapToRequestDto(itemRequest);
    }

}
