package ru.practicum.server.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.booking.dto.BookingDTO;
import ru.practicum.server.booking.dto.BookingResponseDTO;
import ru.practicum.server.booking.service.BookingService;
import ru.practicum.server.comment.dto.CommentDTO;
import ru.practicum.server.item.Item;
import ru.practicum.server.item.ItemMapper;
import ru.practicum.server.item.dto.ItemDTO;
import ru.practicum.server.user.User;
import ru.practicum.server.user.UserMapper;
import ru.practicum.server.user.dto.UserDTO;
import ru.practicum.server.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(properties = {
        "spring.config.name=application-test",
        "spring.config.location=classpath:application-test.properties"
}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class IntegrationItemServiceTest {

    private final BookingService bookingService;

    private final UserService userService;

    private final ItemService itemService;

    private UserDTO ownerDto = UserDTO.builder()
            .name("name")
            .email("user@email.com")
            .build();

    private UserDTO owner2Dto = UserDTO.builder()
            .name("name2")
            .email("user2@email.com")
            .build();
    private UserDTO bookerDto = UserDTO.builder()
            .name("Test booker")
            .email("booker@email.ru")
            .build();

    private ItemDTO itemDto = ItemDTO.builder()
            .name("name")
            .description("description")
            .available(true)
            .build();
    private ItemDTO item2Dto = ItemDTO.builder()
            .name("name2")
            .description("description 2")
            .available(true)
            .build();

    @Test
    void createItemTest() {
        UserDTO owner = userService.createUser(ownerDto);
        User inputUser = UserMapper.toUser(owner);

        ItemDTO item = itemService.createItem(itemDto, owner.getId());
        Item inputItem = ItemMapper.toItem(item, inputUser);

        assertNotNull(inputItem);
        assertNotNull(inputItem.getId());
        assertEquals(inputUser.getId(), inputItem.getOwner().getId());
    }

    @Test
    void findAllItemsByUserIdTest() {
        UserDTO owner = userService.createUser(ownerDto);

        itemService.createItem(itemDto, owner.getId());
        itemService.createItem(item2Dto, owner.getId());

        Long userId = owner.getId();

        List<ItemDTO> itemResponseDtoList = itemService.findAllItemsByUserId(userId, 0, 10);

        assertNotNull(itemResponseDtoList);
        assertEquals(2, itemResponseDtoList.size());
    }

    @Test
    void findItemByIdTest() {
        UserDTO owner = userService.createUser(ownerDto);

        ItemDTO item = itemService.createItem(itemDto, owner.getId());

        Long itemId = item.getId();
        Long userId = owner.getId();

        ItemDTO itemResponseDto = itemService.findItemById(itemId, userId);

        assertNotNull(itemResponseDto);
    }

    @Test
    void updateItemTest() {
        UserDTO owner = userService.createUser(ownerDto);

        ItemDTO item = itemService.createItem(itemDto, owner.getId());

        Long itemId = item.getId();
        Long userId = owner.getId();

        ItemDTO itemResponseDto = itemService.updateItem(item, itemId, userId);

        assertNotNull(itemResponseDto);
    }

    @Test
    void findItemsByRequestTest() {
        UserDTO owner = userService.createUser(ownerDto);

        itemService.createItem(itemDto, owner.getId());
        itemService.createItem(item2Dto, owner.getId());

        Item item3 = new Item();
        item3.setName("Another Item");
        item3.setDescription("test item");
        item3.setAvailable(false);
        ItemDTO inputItem = ItemMapper.toItemDTO(item3);
        itemService.createItem(inputItem, owner.getId());

        List<ItemDTO> result = itemService.findItemsByRequest("Description", 0, 10);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(item -> item.getDescription().equals("description")));
        assertTrue(result.stream().anyMatch(item -> item.getDescription().equals("description 2")));
    }

    @Test
    void addCommentTest() {
        UserDTO owner = userService.createUser(ownerDto);
        UserDTO booker = userService.createUser(bookerDto);

        ItemDTO item = itemService.createItem(itemDto, owner.getId());

        BookingDTO bookingDto = new BookingDTO();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));

        BookingResponseDTO booking = bookingService.createBooking(bookingDto, booker.getId());


        CommentDTO commentDto = new CommentDTO();
        commentDto.setText("Test Comment");

        CommentDTO commentResponseDto = itemService.addComment(commentDto, item.getId(), booker.getId());

        assertNotNull(commentResponseDto.getId());
        assertNotNull(commentResponseDto.getItemId());
        assertEquals(commentDto.getText(), commentResponseDto.getText());
        assertEquals(booker.getName(), commentResponseDto.getAuthorName());
        assertEquals(item.getId(), commentResponseDto.getItemId());
    }

}
