package ru.practicum.shareit.itemrequest.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.itemrequest.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findAllByRequestorIdOrderByCreatedAscTest() {
        User user = userRepository.save(User.builder()
                .name("name")
                .email("email@email.com")
                .build());

        itemRequestRepository.save(ItemRequest.builder()
                .description("description")
                .requestorId(user.getId())
                .created(LocalDateTime.now())
                .build());

        assertThat(itemRequestRepository
                .findAllByRequestorIdOrderByCreatedAsc(user.getId()).size(), equalTo(1));
    }

    @Test
    void findAllByRequestorNotLikeOrderByCreatedAscTest() {
        User user = userRepository.save(User.builder()
                .name("name")
                .email("email@email.com")
                .build());

        itemRequestRepository.save(ItemRequest.builder()
                .description("description")
                .requestorId(user.getId())
                .created(LocalDateTime.now())
                .build());

        assertThat(itemRequestRepository
                .findAllByRequestorIdIsNot(user.getId(), PageRequest.ofSize(10)).size(), equalTo(0));

        User user2 = userRepository.save(User.builder()
                .name("name2")
                .email("email2@email.com")
                .build());

        assertThat(itemRequestRepository
                .findAllByRequestorIdIsNot(user2.getId(), PageRequest.ofSize(10)).size(), equalTo(1));
    }
}