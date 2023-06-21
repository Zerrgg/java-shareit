package ru.practicum.shareit.comment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comment.dto.CommentDTO;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CommentMapperTest {

    public static final LocalDateTime CREATED_DATE = LocalDateTime.now();

    private Item item;
    private User user;
    private Comment comment;
    private CommentDTO commentDTO;

    @BeforeEach
    public void beforeEach() {
        user = new User(5L, "name", "user@emali.com");
        item = new Item(3L, "name", "description", true, user, null);
        comment = new Comment(1L, "comment", item, user, CREATED_DATE);
        commentDTO = new CommentDTO(1L, "comment", "name", CREATED_DATE, 3L);
    }


    @Test
    void ttoCommentTest() {
        Comment result = CommentMapper.toComment(commentDTO, item, user);

        assertNotNull(result);
        assertEquals(commentDTO.getId(), result.getId());
        assertEquals(commentDTO.getText(), result.getText());
        assertEquals(item.getId(), result.getItem().getId());
        assertEquals(user.getId(), result.getAuthor().getId());
        assertEquals(commentDTO.getAuthorName(), result.getAuthor().getName());
        assertEquals(commentDTO.getCreated(), result.getCreated());
    }

    @Test
    void toCommentDTOTest() {
        CommentDTO result = CommentMapper.toCommentDTO(comment);

        assertNotNull(result);
        assertEquals(comment.getId(), result.getId());
        assertEquals(comment.getText(), result.getText());
        assertEquals(comment.getAuthor().getName(), result.getAuthorName());
        assertEquals(comment.getCreated(), result.getCreated());
        assertEquals(comment.getItem().getId(), result.getItemId());
    }

    @Test
    void toDtoListTest() {
        List<CommentDTO> result = CommentMapper
                .toDTOList(Collections.singletonList(comment));

        assertNotNull(result);
        assertEquals(result.get(0).getId(), comment.getId());
        assertEquals(result.get(0).getText(), comment.getText());
        assertEquals(result.get(0).getAuthorName(), comment.getAuthor().getName());
    }
}