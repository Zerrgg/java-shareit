package ru.practicum.shareit.itemrequest.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.itemrequest.ItemRequest;
import ru.practicum.shareit.itemrequest.ItemRequestMapper;
import ru.practicum.shareit.itemrequest.dto.ItemRequestDTO;
import ru.practicum.shareit.itemrequest.repository.ItemRequestRepository;
import ru.practicum.shareit.itemrequest.service.impl.ItemRequestServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestServiceTest {

    public static final long ID = 1L;

    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private ItemRequestService requestService;
    private ItemRequestRepository requestRepository;

    private User user;
    private ItemRequest request;

    @BeforeEach
    public void init() {
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        requestRepository = mock(ItemRequestRepository.class);
        requestService = new ItemRequestServiceImpl(requestRepository, userRepository, itemRepository);

        user = User.builder()
                .id(ID)
                .name("name")
                .email("user@email.com")
                .build();

        request = ItemRequest.builder()
                .id(ID)
                .description("description")
                .requestorId(user.getId())
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void createRequestTest() {
        ItemRequestDTO inputDto = ItemRequestMapper.toItemRequestDTO(request);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(requestRepository.save(any()))
                .thenReturn(request);

        ItemRequestDTO responseDto = requestService.createRequest(ID, inputDto);

        assertNotNull(responseDto);
        assertEquals(ID, responseDto.getId());
        assertEquals(inputDto.getDescription(), responseDto.getDescription());
    }

    @Test
    void findAllByUserIdTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(requestRepository
                .findAllByRequestorIdOrderByCreatedAsc(anyLong()))
                .thenReturn(new ArrayList<>());

        when(itemRepository.findAllByRequestId(anyLong()))
                .thenReturn(new ArrayList<>());

        List<ItemRequestDTO> result = requestService.findAllByUser(ID);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(requestRepository.findAllByRequestorIdIsNot(anyLong(), any()))
                .thenReturn((new ArrayList<>()));

        when(itemRepository.findAllByRequestId(anyLong()))
                .thenReturn(new ArrayList<>());

        List<ItemRequestDTO> result = requestService.findAll(ID, 0, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByIdTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(request));

        when(itemRepository.findAllByRequestId(anyLong()))
                .thenReturn(new ArrayList<>());


        ItemRequestDTO result = requestService.findById(ID, ID);

        assertNotNull(result);
        assertEquals(ID, result.getId());
        assertEquals(request.getDescription(), result.getDescription());
        assertNotNull(result.getItems());
        assertTrue(result.getItems().isEmpty());
    }
}