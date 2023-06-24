package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ItemMapperTest {

    public static final long ID = 1L;
    public static final LocalDateTime CREATED_DATE = LocalDateTime.now();

    private Item item;
    private User user;
    private ItemDTO itemDto;
    private Comment comment;
    private Booking booking;

    @BeforeEach
    public void init() {
        item = new Item(1L, "name", "description", true, user, null);
        itemDto = new ItemDTO(1L, "name", "description", true, null, null, null, null);
        user = new User(3L, "name", "user@emali.com");
        comment = new Comment(5L, "comment", item, user, CREATED_DATE);

        booking = new Booking(7L,
                CREATED_DATE,
                CREATED_DATE.plusDays(10),
                item,
                user,
                BookingStatus.APPROVED);
    }

    @Test
    void toItemWithCommentsDTOTest() {
        List<CommentDTO> commentDTOList = CommentMapper.toDTOList(Collections.singletonList(comment));
        ItemDTO resultWithComments = ItemMapper.toItemWithCommentsDTO(item, commentDTOList);

        assertNotNull(resultWithComments);
        assertFalse(resultWithComments.getComments().isEmpty());
        assertEquals(item.getId(), resultWithComments.getId());
        assertEquals(item.getName(), resultWithComments.getName());
        assertEquals(item.getDescription(), resultWithComments.getDescription());
        assertEquals(item.getAvailable(), resultWithComments.getAvailable());
    }

    @Test
    void ttoItemWithBookingDTOTest() {
        List<CommentDTO> commentDTOList = CommentMapper.toDTOList(Collections.singletonList(comment));
        ItemDTO resultWithBookings = ItemMapper.toItemWithBookingDTO(item, null, null, commentDTOList);

        assertNotNull(resultWithBookings);
        assertFalse(resultWithBookings.getComments().isEmpty());
        assertEquals(item.getId(), resultWithBookings.getId());
        assertEquals(item.getName(), resultWithBookings.getName());
        assertEquals(item.getDescription(), resultWithBookings.getDescription());
        assertEquals(item.getAvailable(), resultWithBookings.getAvailable());
    }

    @Test
    void toItemTest() {
        Item result = ItemMapper.toItem(itemDto, user);

        assertNotNull(result);
        assertEquals(itemDto.getId(), result.getId());
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        assertEquals(itemDto.getAvailable(), result.getAvailable());
    }

    @Test
    void toItemDTOTest() {
        ItemDTO result = ItemMapper.toItemDTO(item);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());
    }
}