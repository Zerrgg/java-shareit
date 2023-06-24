package ru.practicum.shareit.itemrequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.itemrequest.dto.ItemRequestDTO;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ItemRequestMapperTest {

    public static final LocalDateTime CREATED_DATE = LocalDateTime.now();

    private ItemRequest request;
    private ItemRequestDTO requestDto;

    @BeforeEach
    public void init() {
        User requestor = new User(1L, "mame", "user@email.com");
        request = new ItemRequest(1L, "description", requestor.getId(), CREATED_DATE);

        requestDto = ItemRequestDTO.builder()
                .id(request.getId())
                .description("description")
                .items(new ArrayList<>())
                .created(request.getCreated())
                .requestorId(request.getId())
                .build();
    }

    @Test
    void toItemRequestTest() {
        ItemRequest result = ItemRequestMapper.toItemRequest(requestDto);

        assertNotNull(result);
        assertEquals(1L, result.getRequestorId());
        assertEquals(requestDto.getDescription(), result.getDescription());
    }

    @Test
    void toItemRequestDTOTest() {
        ItemRequestDTO result = ItemRequestMapper.toItemRequestDTO(request);

        assertNotNull(result);
        assertEquals(request.getDescription(), result.getDescription());
        assertEquals(request.getId(), result.getId());
        assertEquals(request.getCreated(), result.getCreated());
    }

}