package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static ru.practicum.shareit.booking.BookingStatus.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User user;
    private Item item;
    private User user2;
    private Booking booking;

    @BeforeEach
    public void init() {
        user = User.builder()
                .name("name")
                .email("email@email.com")
                .build();
        userRepository.save(user);

        item = Item.builder()
                .name("item")
                .description("description")
                .available(true)
                .owner(user)
                .build();
        itemRepository.save(item);

        user2 = User.builder()
                .name("name2")
                .email("email2@email.com")
                .build();
        userRepository.save(user2);

        booking = Booking.builder()
                .start(LocalDateTime.now().plusMinutes(1).withNano(0))
                .end(LocalDateTime.now().plusDays(10).withNano(0))
                .item(item)
                .booker(user2)
                .status(WAITING)
                .build();

    }

    @Test
    void findByItemOwnerTest() {
        bookingRepository.save(booking);

        assertThat(bookingRepository
                .findByItemOwner(user, PageRequest.ofSize(10)).size(), equalTo(1));
    }

    @Test
    void findAllByBookerTest() {
        bookingRepository.save(booking);

        assertThat(bookingRepository
                .findByBooker(user2, PageRequest.ofSize(10)).size(), equalTo(1));
    }

    @Test
    void findAllByItemAndStatusOrderByStartAscTest() {
        booking.setStatus(APPROVED);
        bookingRepository.save(booking);

        assertThat(bookingRepository
                .findAllByItemAndStatusOrderByStartAsc(item, APPROVED).size(), equalTo(1));
    }

    @Test
    void findAllByItemInAndStatusOrderByStartAscTest() {
        booking.setStatus(APPROVED);
        bookingRepository.save(booking);

        List<Item> userItems = itemRepository.findAllByOwnerId(user.getId(), PageRequest.of(0, 1));

        assertThat(bookingRepository
                .findAllByItemInAndStatusOrderByStartAsc(userItems, APPROVED).size(), equalTo(1));
    }

    @Test
    void existsBookingByItemAndBookerAndStatusNotAndStartBeforeTest() {
        bookingRepository.save(booking);

        assertFalse(bookingRepository
                .existsBookingByItemAndBookerAndStatusNotAndStartBefore(item, user, REJECTED, LocalDateTime.now()));
    }

}