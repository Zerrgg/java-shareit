package ru.practicum.server.itemrequest.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.server.item.Item;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.itemrequest.ItemRequest;
import ru.practicum.server.user.User;
import ru.practicum.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User itemOwner;
    private Item item;

    @BeforeEach
    public void init() {
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
        List<Item> result = itemRepository.findAllByOwnerIdOrderByIdAsc(itemOwner.getId(), pageRequest);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(item.getOwner(), result.get(0).getOwner());
        assertEquals(item.getName(), result.get(0).getName());
        assertEquals(item.getDescription(), result.get(0).getDescription());
    }

    @Test
    void findAllByRequestIdTest() {
        User user2 = userRepository.save(User.builder()
                .name("name2")
                .email("email2@email.com")
                .build());

        ItemRequest itemRequest = itemRequestRepository.save(ItemRequest.builder()
                .description("item request descr")
                .requestorId(user2.getId())
                .created(LocalDateTime.now())
                .build());

        itemRepository.save(Item.builder()
                .name("name")
                .description("description")
                .available(true)
                .owner(itemOwner)
                .requestId(itemRequest.getId())
                .build());

        assertThat(itemRepository
                .findAllByRequestId(itemRequest.getId()).size(), equalTo(1));
    }
}