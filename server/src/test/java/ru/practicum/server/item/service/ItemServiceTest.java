package ru.practicum.server.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.server.booking.repository.BookingRepository;
import ru.practicum.server.comment.Comment;
import ru.practicum.server.comment.CommentMapper;
import ru.practicum.server.comment.dto.CommentDTO;
import ru.practicum.server.comment.repository.CommentRepository;
import ru.practicum.server.exception.DeniedAccessException;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.exception.ValidateCommentException;
import ru.practicum.server.item.Item;
import ru.practicum.server.item.dto.ItemDTO;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.item.service.impl.ItemServiceImpl;
import ru.practicum.server.user.User;
import ru.practicum.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemServiceTest {

    public static final LocalDateTime DATE = LocalDateTime.now();

    private ItemService itemService;
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;

    private Item item;
    private User owner;
    private User user;
    private ItemDTO itemDTO;
    private Comment comment;
    private CommentDTO commentDTO;

    @BeforeEach
    public void init() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentRepository = mock(CommentRepository.class);
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository);

        user = User.builder()
                .id(2L)
                .name("name")
                .email("user@email.com")
                .build();

        owner = User.builder()
                .id(3L)
                .name("owner")
                .email("user2@email.ru")
                .build();

        item = Item.builder()
                .id(5L)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .requestId(user.getId())
                .build();

        comment = Comment.builder()
                .id(1L)
                .text("comment")
                .item(item)
                .author(user)
                .created(DATE)
                .build();

        itemDTO = ItemDTO.builder()
                .id(5L)
                .name("name")
                .description("description")
                .available(true)
                .lastBooking(null)
                .nextBooking(null)
                .comments(null)
                .requestId(item.getRequestId())
                .build();

        comment = Comment.builder()
                .text("comment")
                .item(item)
                .author(user)
                .created(DATE)
                .build();

        commentDTO = CommentMapper.toCommentDTO(comment);

    }

    @Test
    void createItemTest() {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));

        when(userRepository.findAll())
                .thenReturn(Collections.singletonList(user));

        when(itemRepository.save(any()))
                .thenReturn(item);

        ItemDTO result = itemService.createItem(itemDTO, 3L);

        assertNotNull(result);
        assertEquals(itemDTO.getName(), result.getName());
        assertEquals(itemDTO.getDescription(), result.getDescription());
        assertEquals(itemDTO.getAvailable(), result.getAvailable());
        assertEquals(itemDTO.getRequestId(), result.getRequestId());
    }

    @Test
    void createItemWithNotFoundTest() {
        when(userRepository.findAll())
                .thenReturn(Collections.emptyList());

        Exception e = assertThrows(NotFoundException.class,
                () -> {
                    itemService.createItem(itemDTO, 3L);
                });
        assertNotNull(e);
    }

    @Test
    void addCommentTest() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .existsBookingByItemAndBookerAndStatusNotAndStartBefore(any(), any(), any(), any()))
                .thenReturn(true);

        when(commentRepository.save(any()))
                .thenReturn(comment);

        CommentDTO result = itemService.addComment(commentDTO, 5L, 2L);

        assertNotNull(result);
        assertEquals(commentDTO.getText(), result.getText());
        assertEquals(user.getName(), result.getAuthorName());
    }

    @Test
    void addCommentExceptionTest() {

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(commentRepository.save(any()))
                .thenReturn(comment);

        when(bookingRepository
                .existsBookingByItemAndBookerAndStatusNotAndStartBefore(any(), any(), any(), any()))
                .thenReturn(false);

        Exception e = assertThrows(ValidateCommentException.class,
                () -> {
                    itemService.addComment(commentDTO, 5L, 2L);
                });

        assertNotNull(e);
    }

    @Test
    void updateItemTest() {
        itemDTO.setName("updatedName");
        item.setName("updatedName");

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));

        when(commentRepository.findAllByItemOrderByIdAsc(any()))
                .thenReturn(new ArrayList<>());

        when(itemRepository.save(any()))
                .thenReturn(item);

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        ItemDTO result = itemService.updateItem(itemDTO, item.getId(), owner.getId());

        assertNotNull(result);
        assertEquals(itemDTO.getId(), result.getId());
        assertEquals(itemDTO.getName(), result.getName());
    }

    @Test
    void updateItemDeniedAccessExceptionTest() {
        item.setOwner(owner);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(commentRepository.findAllByItemOrderByIdAsc(any()))
                .thenReturn(new ArrayList<>());

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        Exception e = assertThrows(DeniedAccessException.class,
                () -> {
                    itemService.updateItem(itemDTO, item.getId(), user.getId());
                });

        assertNotNull(e);
    }

    @Test
    void findItemByIdTest() {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        when(commentRepository.findAllByItemOrderByIdAsc(any()))
                .thenReturn(new ArrayList<>());

        ItemDTO result = itemService.findItemById(5L, 3L);

        assertNotNull(result);
        assertEquals(5L, result.getId());
        assertTrue(result.getComments().isEmpty());
    }

    @Test
    void findAllItemsByUserIdTest() {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));

        when(itemRepository.findAll())
                .thenReturn(Collections.emptyList());

        List<ItemDTO> result = itemService.findAllItemsByUserId(3L, 0, 10);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findItemsByRequestTest() {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(itemRepository.search(anyString(), any()))
                .thenReturn(new ArrayList<>());

        List<ItemDTO> result = itemService.findItemsByRequest("request", 0, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}