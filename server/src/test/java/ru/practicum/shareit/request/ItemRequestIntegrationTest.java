package ru.practicum.shareit.request;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(properties = {"spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:shareit"})
public class ItemRequestIntegrationTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private RequestService requestService;

    private ItemRequestController itemRequestController;

    private User user;

    @BeforeEach
    public void setup() {
        itemRequestController = new ItemRequestController(requestService);

        user = new User();
        user.setName("Sergey");
        user.setEmail("ser@mail.com");
        userRepository.save(user);
    }

    @Test
    public void addRequestIntegrationTest() {

        NewItemRequestDto newRequestDto = new NewItemRequestDto();
        newRequestDto.setDescription("Нужены ролики");
        newRequestDto.setCreated(LocalDateTime.now());

        ItemRequestDto createdDto = itemRequestController.addRequest(user.getId(), newRequestDto);

        assertNotNull(createdDto);
        assertEquals("Нужены ролики", createdDto.getDescription());
        assertEquals(1, createdDto.getId());
        assertNotNull(createdDto.getCreated());

        // Проверяем, что запрос сохранен в базе данных
        Optional<ItemRequest> requestFromDb = requestRepository.findById(createdDto.getId());
        assertTrue(requestFromDb.isPresent());
        assertEquals("Нужены ролики", requestFromDb.get().getDescription());
        assertEquals(user.getId(), requestFromDb.get().getRequestor().getId());

                assertThrows(NotFoundException.class, () -> {
            itemRequestController.addRequest(999L, newRequestDto);
        });
    }

    @Test
    public void findAllRequestsIntegrationTest() {

        NewItemRequestDto request1 = new NewItemRequestDto();
        request1.setDescription("Требуется колонка");
        request1.setCreated(LocalDateTime.now());

        NewItemRequestDto request2 = new NewItemRequestDto();
        request2.setDescription("Нужна куртка");
        request2.setCreated(LocalDateTime.now());

        itemRequestController.addRequest(user.getId(), request1);
        itemRequestController.addRequest(user.getId(), request2);

        List<ItemRequestDto> requests = itemRequestController.findAllRequests();

        assertNotNull(requests);
        assertEquals(2, requests.size());

        List<String> listDescriptions = requests.stream()
                .map(ItemRequestDto::getDescription)
                .toList();
        assertTrue(listDescriptions.contains("Требуется колонка"));
        assertTrue(listDescriptions.contains("Нужна куртка"));
    }

    @Test
    public void getRequestsUserIdIntegrationTest() {

        User user2 = new User();
        user2.setName("Slava");
        user2.setEmail("slava@mail.com");
        userRepository.save(user2);

        NewItemRequestDto newRequestDto1 = new NewItemRequestDto();
        newRequestDto1.setDescription("Запрос для Sergey");
        newRequestDto1.setCreated(LocalDateTime.now());

        NewItemRequestDto newRequestDto2 = new NewItemRequestDto();
        newRequestDto2.setDescription("Запрос для Slava");
        newRequestDto2.setCreated(LocalDateTime.now());

        // Запрос для для первого пользователя
        itemRequestController.addRequest(user.getId(), newRequestDto1);
        // Запрос для для второго пользователя
        itemRequestController.addRequest(user2.getId(), newRequestDto2);

        List<ItemRequestDto> sergeyRequests = itemRequestController.getRequestsUserId(user.getId());

        assertEquals(1, sergeyRequests.size());
        assertEquals("Запрос для Sergey", sergeyRequests.get(0).getDescription());

        List<ItemRequestDto> slavaRequests = itemRequestController.getRequestsUserId(user2.getId());
        assertEquals(1, slavaRequests.size());
        assertEquals("Запрос для Slava", slavaRequests.get(0).getDescription());

                assertThrows(NotFoundException.class, () -> {
            itemRequestController.getItemRequestId(user.getId(), 999L);
        });

    }

    @Test
    public void getItemRequestIdIntegrationTest() {

        NewItemRequestDto newRequest = new NewItemRequestDto();
        newRequest.setDescription("Требуется чайный сервиз");
        newRequest.setCreated(LocalDateTime.now());

        ItemRequestDto requestDto1 = itemRequestController.addRequest(user.getId(), newRequest);

        ItemRequestDto requestDto2 = itemRequestController.getItemRequestId(user.getId(), requestDto1.getId());

        assertNotNull(requestDto2);
        assertEquals(requestDto1.getId(), requestDto2.getId());
        assertEquals("Требуется чайный сервиз", requestDto2.getDescription());
        assertNotNull(requestDto2.getCreated());
    }

}
