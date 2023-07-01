package ru.practicum.server.comment.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.server.comment.Comment;
import ru.practicum.server.item.Item;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.user.User;
import ru.practicum.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private Item item;
    private User itemOwner;
    private User commentOwner;
    private Comment comment;

    @BeforeEach
    public void init() {

        itemOwner = User.builder()
                .name("name")
                .email("email@email.com")
                .build();

        item = Item.builder()
                .name("item")
                .description("description")
                .available(true)
                .owner(itemOwner)
                .build();

        commentOwner = User.builder()
                .name("name2")
                .email("email2@email.com")
                .build();

        comment = Comment.builder()
                .text("comment")
                .item(item)
                .author(commentOwner)
                .created(LocalDateTime.now())
                .build();

        userRepository.save(itemOwner);
        itemRepository.save(item);
        userRepository.save(commentOwner);
        commentRepository.save(comment);

    }

    @Test
    void findAllByItemOrderByIdAscTest() {
        List<Comment> result = commentRepository.findAllByItemOrderByIdAsc(item);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(comment.getId(), result.get(0).getId());
        assertEquals(comment.getText(), result.get(0).getText());
        assertEquals(comment.getAuthor(), commentOwner);
        assertEquals(comment.getItem(), item);
    }

    @Test
    void findByItemInTest() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Item> userItems = itemRepository.findAllByOwnerId(itemOwner.getId(), pageRequest);
        List<Comment> result = commentRepository.findByItemIn(userItems);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(comment.getId(), result.get(0).getId());
        assertEquals(comment.getText(), result.get(0).getText());
        assertEquals(comment.getAuthor(), commentOwner);
        assertEquals(comment.getItem(), item);
    }

}