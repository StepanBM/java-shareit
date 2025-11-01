package ru.practicum.shareit.item;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.item.CommentMapper.mapToComment;
import static ru.practicum.shareit.item.CommentMapper.mapToCommentDto;
import static ru.practicum.shareit.item.ItemMapper.*;
import static ru.practicum.shareit.item.ItemMapper.mapToItemCommentDto;
import static ru.practicum.shareit.item.ItemMapper.updateItemFields;

@Qualifier("ItemDbService")
@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final RequestRepository requestRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository,
                           UserRepository userRepository,
                           CommentRepository commentRepository,
                           BookingRepository bookingRepository,
                           RequestRepository requestRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
        this.requestRepository = requestRepository;
    }

    @Override
    public List<ItemWithCommentDto> findAllItems(long userId) {
        userRepository.findById(userId);
        List<Item> items = itemRepository.findItemsByOwnerId(userId);
        List<ItemWithCommentDto> itemWithCommentDto = new ArrayList<>();
        for (Item item : items) {
            List<Comment> comments = commentRepository.findAllByItemId(item.getId());
            List<CommentDto> commentDto = comments.stream()
                    .map(CommentMapper::mapToCommentDto).toList();

            LocalDateTime now = LocalDateTime.now();

            // Поиск последнего бронирования
            Optional<Booking> lastBookingOptional = bookingRepository.findByItemIdAndEndIsAfterOrderByEndDesc(item.getId(), now);
            // Поиск следующего бронирования
            Optional<Booking> nextBookingOptional = bookingRepository.findByItemIdAndStartAfterOrderByStartAsc(item.getId(), now);

            LocalDateTime lastBooking = lastBookingOptional.map(Booking::getEnd).orElse(null);
            LocalDateTime nextBooking = nextBookingOptional.map(Booking::getStart).orElse(null);

            ItemWithCommentDto dto = mapToItemCommentDto(item, commentDto, lastBooking, nextBooking);

            itemWithCommentDto.add(dto);
        }
        return itemWithCommentDto;
    }

    @Override
    @Transactional
    public ItemDto addItem(NewItemRequest request) {
       // log.debug("Начинается добавление вещи по запросу {}", request);

        User user = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + request.getOwnerId() + " не найден"));

        Item item = mapToItem(request);
        item.setOwner(user);

        if (request.getRequestId() != null && request.getRequestId() > 0L) {
            ItemRequest req = requestRepository.findById(request.getRequestId()).orElseThrow(
                    () -> new NotFoundException("Запрос с ID " + request.getRequestId() + " не существует.")
            );
            item.setRequest(req);
        }

       // log.debug("Запрос на добавление вещи конвертирован в объект класса Item {}", item);
        item = itemRepository.save(item);
       // log.debug("Добавлена вещь {}", item);
        return mapToItemDto(item);
    }

    @Override
    public ItemWithCommentDto getItemId(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена или не принадлежит владельцу"));
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        List<CommentDto> commentDto = comments.stream()
                .map(CommentMapper::mapToCommentDto).toList();

        LocalDateTime now = LocalDateTime.now();

        // Поиск последнего бронирования
        Optional<Booking> lastBookingOptional = bookingRepository.findByItemIdAndEndIsAfterOrderByEndDesc(itemId, now);
        // Поиск следующего бронирования
        Optional<Booking> nextBookingOptional = bookingRepository.findByItemIdAndStartAfterOrderByStartAsc(itemId, now);

        LocalDateTime lastBooking = lastBookingOptional.map(Booking::getEnd).orElse(null);
        LocalDateTime nextBooking = nextBookingOptional.map(Booking::getStart).orElse(null);

        return mapToItemCommentDto(item, commentDto, lastBooking, nextBooking);
    }

    @Override
    @Transactional
    public ItemDto updateItem(UpdateItemRequest request) {
        Item existingItem = itemRepository.findByIdAndOwnerId(request.getId(), request.getOwnerId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена или не принадлежит владельцу"));
        Item itemToUpdate = updateItemFields(existingItem, request);
       // log.debug("Начинается обновление вещи на основе данных из DTO по запросу {}", request);
        Item updatedItem = itemRepository.save(itemToUpdate);
       // log.debug("Обновлена вещь {}", updatedItem);
        return mapToItemDto(updatedItem);
    }

    @Override
    public List<ItemDto> searchItems(long userId, String query) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с данным id= " + userId + " не найден"));
        //log.debug("Начинается поиск вещи по запросу {}", query);
        String queryLower = "%" + query.toLowerCase() + "%";
        return itemRepository.searchItems(userId, queryLower).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public CommentDto addComment(long userId, NewCommentRequest request, long itemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с данным id= " + userId + " не найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с данным id= " + itemId + " не найден"));

        // Проверка что пользователь брал вещь в аренду
        boolean hasPastBooking = bookingRepository.existsByItemIdAndBookerIdAndEndBefore(item.getId(), user.getId(), LocalDateTime.now());

        if (!hasPastBooking) {
            throw new IllegalStateException("Пользователь не брал эту вещь в аренду");
        }
        Comment comment = mapToComment(request);
        comment.setAuthorName(user);
        comment.setCreated(LocalDateTime.now());
        comment.setItem(item);

        return mapToCommentDto(commentRepository.save(comment));
    }

}
