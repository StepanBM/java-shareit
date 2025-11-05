package ru.practicum.shareit.item;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(properties = {"spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:shareit"})
public class ItemIntegrationTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemService itemService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private ItemController itemController;

    private User user;

    @BeforeEach
    public void setup() {
        itemController = new ItemController(itemService);

        user = new User();
        user.setName("Sergey");
        user.setEmail("ser@mail.com");
        userRepository.save(user);
    }

    @Test
    public void addItemIntegrationTest() {

        NewItemRequest request = new NewItemRequest();
        request.setName("Насос");
        request.setDescription("Классный");
        request.setAvailable(true);
        request.setOwnerId(user.getId());
        request.setRequestId(null);

        ItemDto itemDto = itemController.addItem(user.getId(), request);

        assertNotNull(itemDto);
        assertEquals("Насос", itemDto.getName());
        assertEquals("Классный", itemDto.getDescription());
        assertTrue(itemDto.getAvailable());
        assertEquals(user.getId(), itemDto.getOwnerId());

        Optional<Item> itemOpt = itemRepository.findById(itemDto.getId());
        assertTrue(itemOpt.isPresent());
        Item item = itemOpt.get();
        assertEquals("Насос", item.getName());
    }

    @Test
    public void addItemUserNotFoundIntegrationTest() {
        NewItemRequest request = new NewItemRequest();
        request.setName("Насос");
        request.setDescription("Новый");
        request.setAvailable(true);
        request.setOwnerId(999L);

        assertThrows(NotFoundException.class, () -> {
            itemService.addItem(request);
        });
    }

    @Test
    public void addItemRequestNotFoundIntegrationTest() {
        NewItemRequest request = new NewItemRequest();
        request.setName("Насос");
        request.setDescription("Новый");
        request.setAvailable(true);
        request.setOwnerId(user.getId());
        request.setRequestId(999L);

        assertThrows(NotFoundException.class, () -> {
            itemService.addItem(request);
        });
    }

    @Test
    public void findAllItemsIntegrationTest() {

        Item item = new Item();
        item.setName("Телевизор");
        item.setDescription("Современный");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        Comment comment = new Comment();
        comment.setText("Хорошо показывает");
        comment.setItem(item);
        comment.setAuthorName(user);
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);

        List<ItemWithCommentDto> listItemCommentDto = itemController.findAllItems(user.getId());

        assertFalse(listItemCommentDto.isEmpty());
        ItemWithCommentDto itemCommentDto = listItemCommentDto.get(0);
        assertEquals(item.getId(), itemCommentDto.getId());
        assertNotNull(itemCommentDto.getComments());
        assertEquals(1, itemCommentDto.getComments().size());
        assertEquals("Хорошо показывает", itemCommentDto.getComments().get(0).getText());
    }

    @Test
    public void getItemIdIntegrationTest() {

        // Пользователь являющийся автором комментария
        User author = new User();
        author.setName("Anna");
        author.setEmail("anna1977@mail.com");
        userRepository.save(author);

        Item item = new Item();
        item.setName("Планшет");
        item.setDescription("Описание планшета");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        Comment comment = new Comment();
        comment.setText("Превосходная вещь");
        comment.setItem(item);
        comment.setAuthorName(author);
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);

        // Бронирование, для того чтобы пользователь мог оставить комментарий
        Booking pastBooking  = new Booking();
        pastBooking.setItem(item);
        pastBooking.setBooker(author);
        pastBooking.setStart(LocalDateTime.now().minusDays(25));
        pastBooking.setEnd(LocalDateTime.now().minusDays(10));
        pastBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(pastBooking);

        Booking futureBooking = new Booking();
        futureBooking.setItem(item);
        futureBooking.setBooker(user);
        futureBooking.setStart(LocalDateTime.now().plusDays(10));
        futureBooking.setEnd(LocalDateTime.now().plusDays(25));
        futureBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(futureBooking);

        ItemWithCommentDto itemCommentDto = itemController.getItemId(user.getId(), item.getId());

        assertNotNull(itemCommentDto, "Результат не должен быть null");
        assertEquals(item.getId(), itemCommentDto.getId(), "id вещи должны совпадать");
        assertEquals("Планшет", itemCommentDto.getName(), "Название вещи должны совпадать");
        assertEquals("Описание планшета", itemCommentDto.getDescription(), "Описание должно совпадать");
        assertEquals(item.getRequest() != null ? item.getRequest().getId() : null, itemCommentDto.getRequest(), "Request ID должен совпадать или быть null");

        assertNotNull(itemCommentDto.getComments(), "Комментарии не должны быть null");
        assertFalse(itemCommentDto.getComments().isEmpty(), "Комментарии должны быть");
        CommentDto commentDto = itemCommentDto.getComments().get(0);
        assertEquals("Превосходная вещь", commentDto.getText(), "Текст комментария должен совпадать");
        assertEquals("Anna", commentDto.getAuthorName(), "Имя автора должно совпадать");
        assertNotNull(commentDto.getCreated(), "Дата создания комментария должна быть");

        assertNotNull(itemCommentDto.getLastBooking());
        assertNotNull(itemCommentDto.getNextBooking());
    }

    @Test
    public void getItemIdNotFoundIntegrationTest() {
        assertThrows(NotFoundException.class, () -> {
            itemService.getItemId(user.getId(), 999L);
        });
    }

    @Test
    public void getItemIdNoCommentsIntegrationTest() {

        Item newItem = new Item();
        newItem.setName("Без комментариев");
        newItem.setDescription("Нет комментариев");
        newItem.setAvailable(true);
        newItem.setOwner(user);
        itemRepository.save(newItem);

        ItemWithCommentDto itemCommentDto = itemService.getItemId(user.getId(), newItem.getId());

        assertNotNull(itemCommentDto);
        assertEquals(newItem.getId(), itemCommentDto.getId());
        assertTrue(itemCommentDto.getComments().isEmpty());
        assertNull(itemCommentDto.getLastBooking());
        assertNull(itemCommentDto.getNextBooking());
    }

    @Test
    public void updateItemIntegrationTest() {

        Item item = new Item();
        item.setName("Кружка");
        item.setDescription("Большая");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        UpdateItemRequest updateRequest = new UpdateItemRequest();
        updateRequest.setName("Бокал");
        updateRequest.setDescription("Хрустальный");
        updateRequest.setAvailable(false);

        ItemDto updatedDto = itemController.updateItem(user.getId(), item.getId(), updateRequest);

        assertEquals("Бокал", updatedDto.getName());
        assertEquals("Хрустальный", updatedDto.getDescription());
        assertFalse(updatedDto.getAvailable());

        // Попытка обновить чужую вещь
        User strangerUser = new User();
        strangerUser.setName("Bob");
        strangerUser.setEmail("bobobo@mail.com");
        userRepository.save(strangerUser);

        UpdateItemRequest errorRequest = new UpdateItemRequest();
        errorRequest.setName("Чужак");

        assertThrows(NotFoundException.class, () -> {
            itemController.updateItem(strangerUser.getId(), item.getId(), errorRequest);
        });
    }

    @Test
    public void updateItemNewNameIntegrationTest() {

        Item item = new Item();
        item.setName("Кружка");
        item.setDescription("Большая");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        UpdateItemRequest request = new UpdateItemRequest();
        request.setId(item.getId());
        request.setName("Бокал");
        request.setDescription(null);
        request.setAvailable(null);
        request.setOwnerId(user.getId());

        ItemDto updatedDto = itemService.updateItem(request);

        assertEquals("Бокал", updatedDto.getName());

        Item entity = itemRepository.findById(item.getId()).orElseThrow();
        assertEquals("Бокал", entity.getName());
        assertEquals("Большая", entity.getDescription());
    }

    @Test
    public void updateItemNotFoundIntegrationTest() {
        UpdateItemRequest request = new UpdateItemRequest();
        request.setId(999L);
        request.setName("Name");
        request.setOwnerId(user.getId());

        assertThrows(NotFoundException.class, () -> {
            itemService.updateItem(request);
        });
    }

    @Test
    public void updateItemNotUserIntegrationTest() {
        Item item = new Item();
        item.setName("Кружка");
        item.setDescription("Большая");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        User strangerUser = new User();
        strangerUser.setName("Bob");
        strangerUser.setEmail("bobobo@mail.com");
        userRepository.save(strangerUser);

        UpdateItemRequest request = new UpdateItemRequest();
        request.setId(item.getId());
        request.setName("Какаята вещь");
        request.setOwnerId(strangerUser.getId());

        assertThrows(NotFoundException.class, () -> {
            itemService.updateItem(request);
        });
    }

    @Test
    public void searchItemsIntegrationTest() {

        Item item = new Item();
        item.setName("Электро гитара");
        item.setDescription("Качественный звук");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        List<ItemDto> listItemDto = itemController.searchItems(user.getId(), "электро");

        assertFalse(listItemDto.isEmpty());
        assertEquals("Электро гитара", listItemDto.get(0).getName());

        assertThrows(NotFoundException.class, () -> {
            itemController.searchItems(999L, "электро");
        });
    }

    @Test
    public void searchItemsEmptyListIfNoMatchIntegrationTest() {

        Item item = new Item();
        item.setName("Электро гитара");
        item.setDescription("Качественный звук");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        List<ItemDto> listItemDto = itemService.searchItems(user.getId(), "wertyuij");
        assertNotNull(listItemDto);
        assertTrue(listItemDto.isEmpty());
    }

    @Test
    public void searchItemsUserNotFoundIntegrationTest() {

        Item item = new Item();
        item.setName("Электро гитара");
        item.setDescription("Качественный звук");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        assertThrows(NotFoundException.class, () -> {
            itemService.searchItems(999L, "гитара");
        });
    }

    @Test
    public void searchItemsRegisterQueryIntegrationTest() {

        Item item = new Item();
        item.setName("Электро гитара");
        item.setDescription("Качественный звук");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        List<ItemDto> listItemDto = itemService.searchItems(user.getId(), "ЭЛЕКТРО");

        assertEquals(1, listItemDto.size());
        assertEquals("Электро гитара", listItemDto.get(0).getName());
    }

    @Test
    public void addCommentIntegrationTest() {
        // Пользователь являющийся автором комментария
        User author = new User();
        author.setName("Slava");
        author.setEmail("slavaaa@mail.com");
        userRepository.save(author);

        Item item = new Item();
        item.setName("Стул");
        item.setDescription("Красное дерево");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        // Бронирование, для того чтобы пользователь мог оставить комментарий
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(author);
        booking.setStart(LocalDateTime.now().minusDays(25));
        booking.setEnd(LocalDateTime.now().minusDays(10));
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        NewCommentRequest request = new NewCommentRequest();
        request.setUserId(author.getId());
        request.setItemId(item.getId());
        request.setText("Очень удобный и красивый");

        CommentDto commentDto = itemController.addComment(author.getId(), request, item.getId());

        assertNotNull(commentDto, "комментарий dto не должен быть равным null");
        assertEquals("Очень удобный и красивый", commentDto.getText(), "Текст комментария должен совпадать");
        assertEquals(author.getName(), commentDto.getAuthorName(), "Имя автора должно совпадать");
        assertNotNull(commentDto.getCreated(), "Дата создания комментария должна быть установлена");
        assertTrue(commentDto.getId() > 0, "id комментария должен быть больше 0");

        // Проверяем, что комментарий сохранен в базе данных
        Optional<Comment> commentOpt = commentRepository.findById(commentDto.getId());
        assertTrue(commentOpt.isPresent(), "Комментарий должен быть сохранен в базе");
        Comment comment = commentOpt.get();
        assertEquals("Очень удобный и красивый", comment.getText(), "Текст комментария в базе данных должен совпадать");
        assertEquals(author.getId(), comment.getAuthorName().getId(), "id автора в базе данных должен совпадать");
        assertEquals(item.getId(), comment.getItem().getId(), "id вещи в базе данных должен совпадать");
    }

    @Test
    public void addCommentUserNotFoundIntegrationTest() {
        Item item = new Item();
        item.setName("Стул");
        item.setDescription("Красное дерево");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        NewCommentRequest request = new NewCommentRequest();
        request.setUserId(999L);
        request.setItemId(item.getId());
        request.setText("Супер");

        assertThrows(NotFoundException.class, () -> {
            itemService.addComment(999L, request, item.getId());
        });
    }

    @Test
    public void addCommentItemNotFoundIntegrationTest() {
        NewCommentRequest request = new NewCommentRequest();
        request.setUserId(user.getId());
        request.setItemId(999L);
        request.setText("Супер");

        assertThrows(NotFoundException.class, () -> {
            itemService.addComment(user.getId(), request, 999L);
        });
    }

    @Test
    public void addCommentUserNotBrokeItemIntegrationTest() {
        // Создаем пользователя без бронирования
        User newUser = new User();
        newUser.setName("Sava");
        newUser.setEmail("sava999@mail.com");
        userRepository.save(newUser);

        Item item = new Item();
        item.setName("Стул");
        item.setDescription("Красное дерево");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        NewCommentRequest request = new NewCommentRequest();
        request.setUserId(newUser.getId());
        request.setItemId(item.getId());
        request.setText("Супер");

        assertThrows(IllegalStateException.class, () -> {
            itemService.addComment(newUser.getId(), request, item.getId());
        });
    }

}
