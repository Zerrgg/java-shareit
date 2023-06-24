package ru.practicum.shareit.itemrequest.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemShortDTO;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.itemrequest.dto.ItemRequestDTO;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(properties = {
        "spring.config.name=application-test",
        "spring.config.location=classpath:application-test.properties"
}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class IntegrationItemRequestServiceTest {

    private final ItemRequestService itemRequestService;

    private final ItemService itemService;

    private final UserService userService;

    private UserDTO ownerDto = UserDTO.builder()
            .name("name")
            .email("user@email.com")
            .build();
    private UserDTO owner2Dto = UserDTO.builder()
            .name("name2")
            .email("user2@email.com")
            .build();
    private UserDTO requestorDto = UserDTO.builder()
            .name("Test requestor")
            .email("requestor@email.ru")
            .build();
    ;
    private ItemDTO itemDto = ItemDTO.builder()
            .name("name")
            .description("description")
            .available(true)
            .build();
    private ItemDTO item2Dto = ItemDTO.builder()
            .name("name2")
            .description("description2")
            .available(true)
            .build();
    private ItemRequestDTO requestDto = ItemRequestDTO.builder()
            .description("requestDescription")
            .build();
    private ItemRequestDTO request2Dto = ItemRequestDTO.builder()
            .description("requestDescriptionSecond")
            .build();
    private ItemRequestDTO request3Dto = ItemRequestDTO.builder()
            .description("requestDescriptionThird")
            .build();

    @Test
    void createRequestTest() {
        UserDTO owner = userService.createUser(ownerDto);
        UserDTO requestor = userService.createUser(requestorDto);
        itemService.createItem(itemDto, owner.getId());

        ItemRequestDTO itemRequestDTO = itemRequestService.createRequest(requestor.getId(), requestDto);

        assertNotNull(itemRequestDTO);
        assertNotNull(itemRequestDTO.getId());
        assertNotNull(itemRequestDTO.getCreated());
        assertNotNull(itemRequestDTO.getItems());
        assertEquals(requestDto.getDescription(), itemRequestDTO.getDescription());
        assertEquals(itemRequestDTO.getRequestorId(), requestor.getId());
    }

    @Test
    void findAllByUserTest() {
        UserDTO owner = userService.createUser(ownerDto);
        UserDTO requestor = userService.createUser(requestorDto);
        itemService.createItem(itemDto, owner.getId());
        itemService.createItem(item2Dto, owner.getId());
        itemRequestService.createRequest(requestor.getId(), request2Dto);
        ItemRequestDTO request3 = itemRequestService.createRequest(requestor.getId(), request3Dto);

        List<ItemRequestDTO> requestDTOList = itemRequestService.findAllByUser(requestor.getId());

        assertNotNull(requestDTOList);
        assertEquals(2, requestDTOList.size());
        assertTrue(requestDTOList.contains(request3));
    }

    @Test
    void GetOtherUsersItemRequestsTest() {
        UserDTO owner = userService.createUser(ownerDto);
        UserDTO owner2 = userService.createUser(owner2Dto);
        UserDTO requestor = userService.createUser(requestorDto);
        itemService.createItem(itemDto, owner.getId());
        itemService.createItem(item2Dto, owner2.getId());
        itemRequestService.createRequest(requestor.getId(), request2Dto);
        itemRequestService.createRequest(requestor.getId(), request3Dto);

        List<ItemRequestDTO> requestDTOList = itemRequestService.findAll(owner2.getId(), 0, 10);

        assertNotNull(requestDTOList);
        assertNotNull(requestDTOList.get(0).getDescription());
        assertEquals(2, requestDTOList.size());
    }

    @Test
    void findByIdTest() {
        UserDTO owner = userService.createUser(ownerDto);
        UserDTO requestor = userService.createUser(requestorDto);
        ItemDTO item = itemService.createItem(itemDto, owner.getId());
        ItemDTO item2 = itemService.createItem(item2Dto, owner.getId());
        ItemRequestDTO request = itemRequestService.createRequest(requestor.getId(), requestDto);

        User inputUser = UserMapper.toUser(owner);
        Item inputItem = ItemMapper.toItem(item, inputUser);
        Item inputItem2 = ItemMapper.toItem(item2, inputUser);
        ItemShortDTO itemShortDTO = ItemMapper.toItemShortDTO(inputItem2);

        ItemShortDTO itemShortDTOSecond = ItemShortDTO.builder()
                .id(5L)
                .name("Ivan")
                .description("Bla bla bla bla bla")
                .available(true)
                .requestId(1L)
                .build();

        ItemRequestDTO itemRequestDTO = itemRequestService.findById(request.getId(), owner.getId());
        itemRequestDTO.setItems(List.of(ItemMapper.toItemShortDTO(inputItem), itemShortDTO, itemShortDTOSecond));

        assertNotNull(itemRequestDTO);
        assertEquals(3, itemRequestDTO.getItems().size());
        assertEquals(request.getId(), itemRequestDTO.getId());
    }
}