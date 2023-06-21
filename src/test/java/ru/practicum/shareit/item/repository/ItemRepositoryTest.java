package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User itemOwner;
    private Item item;

    @BeforeEach
    public void beforeEach() {
        itemOwner = User.builder()
                .name("owner")
                .email("owner@email.com")
                .build();
        userRepository.save(itemOwner);

        item = Item.builder()
                .name("item")
                .description("description")
                .available(true)
                .owner(itemOwner)
                .build();
        itemRepository.save(item);

    }

    @Test
    void searchTest() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Item> result = itemRepository.search("description", pageRequest);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains(item));
    }

    @Test
    void findAllByOwnerIdTest() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Item> result = itemRepository.findAllByOwnerId(itemOwner.getId(), pageRequest);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(item.getOwner(), result.get(0).getOwner());
        assertEquals(item.getName(), result.get(0).getName());
        assertEquals(item.getDescription(), result.get(0).getDescription());
    }

}