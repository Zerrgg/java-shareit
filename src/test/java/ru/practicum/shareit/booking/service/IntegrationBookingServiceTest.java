package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.dto.BookingResponseDTO;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateBookingException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

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
class IntegrationBookingServiceTest {

    private final BookingRepository bookingRepository;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingService bookingService;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;

    @BeforeEach
    public void init() {

        owner = User.builder()
                .name("name")
                .email("user@email.com")
                .build();

        booker = User.builder()
                .name("Test Booker")
                .email("Booker@email.ru")
                .build();

        item = Item.builder()
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        booking = Booking.builder()
                .start(LocalDateTime.now().plusMinutes(10).withNano(0))
                .end(LocalDateTime.now().plusDays(10).withNano(0))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void CreateBookingTest() {
        userRepository.save(owner);
        itemRepository.save(item);
        userRepository.save(booker);

        BookingDTO bookingDto = BookingMapper.toBookingDto(booking);

        BookingResponseDTO bookingResponseDto = bookingService.createBooking(bookingDto, booker.getId());

        assertNotNull(bookingResponseDto.getId());
        assertEquals(bookingDto.getStart(), bookingResponseDto.getStart());
        assertEquals(bookingDto.getEnd(), bookingResponseDto.getEnd());
        assertEquals(item.getId(), bookingResponseDto.getItem().getId());
        assertEquals(booker.getId(), bookingResponseDto.getBooker().getId());
    }

    @Test
    void CreateBookingForOwnerTest() {
        userRepository.save(owner);
        itemRepository.save(item);

        BookingDTO bookingDto = BookingMapper.toBookingDto(booking);

        assertThrows(NotFoundException.class,
                () -> {
                    bookingService.createBooking(bookingDto, owner.getId());
                });
    }

    @Test
    void CreateBookingAvailableFalseTest() {
        userRepository.save(owner);
        item.setAvailable(false);
        itemRepository.save(item);
        userRepository.save(booker);

        BookingDTO bookingDto = BookingMapper.toBookingDto(booking);

        assertThrows(ValidateBookingException.class,
                () -> {
                    bookingService.createBooking(bookingDto, booker.getId());
                });
    }

    @Test
    void updateBookingTest() {
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);

        bookingRepository.save(booking);

        BookingResponseDTO bookingResponseDto = bookingService.updateBooking(booking.getId(), true, owner.getId());

        assertEquals(BookingStatus.APPROVED, bookingResponseDto.getStatus());
    }

    @Test
    void updateBookingREJECTEDTest() {
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);

        bookingRepository.save(booking);

        BookingResponseDTO bookingResponseDto = bookingService.updateBooking(booking.getId(), false, owner.getId());

        assertEquals(BookingStatus.REJECTED, bookingResponseDto.getStatus());
    }

    @Test
    void updateBookingTwoTest() {
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);

        bookingRepository.save(booking);

        BookingResponseDTO bookingResponseDto = bookingService.updateBooking(booking.getId(), true, owner.getId());

        assertEquals(BookingStatus.APPROVED, bookingResponseDto.getStatus());
        assertThrows(ValidateBookingException.class,
                () -> {
                    bookingService.updateBooking(booking.getId(), true, owner.getId());
                });
    }

    @Test
    void updateBookingDataFalseTest() {
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);

        BookingDTO bookingDto = new BookingDTO();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusMinutes(1));

        Booking booking = BookingMapper.toBooking(bookingDto, item, booker);
        bookingRepository.save(booking);

        BookingResponseDTO bookingResponseDto = bookingService.updateBooking(booking.getId(), true, owner.getId());

        assertEquals(BookingStatus.APPROVED, bookingResponseDto.getStatus());
        assertThrows(ValidateBookingException.class,
                () -> {
                    bookingService.updateBooking(booking.getId(), true, owner.getId());
                });
    }

    @Test
    void findBookingByIdTest() {
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);

        bookingRepository.save(booking);

        BookingResponseDTO bookingResponseDto = bookingService.findBookingById(owner.getId(), booking.getId());

        assertNotNull(bookingResponseDto);
        assertEquals(booking.getId(), bookingResponseDto.getId());
    }

    @Test
    void findBookingByIdNotFoundTest() {
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);

        bookingRepository.save(booking);

        assertThrows(NotFoundException.class,
                () -> {
                    bookingService.findBookingById(owner.getId(), 5L);
                });
    }

    @Test
    void findBookingsByItemsOwnerForOwnerTest() {
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);

        bookingRepository.save(booking);

        List<BookingResponseDTO> bookingResponseDtoList = bookingService.findBookingsByItemsOwner("all", owner.getId(), 0, 10);

        assertNotNull(bookingResponseDtoList);
        assertFalse(bookingResponseDtoList.isEmpty());
        assertEquals(1, bookingResponseDtoList.size());
    }

    @Test
    void findBookingsByUserTest() {
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);

        bookingRepository.save(booking);

        assertThrows(NotFoundException.class,
                () -> {
                    bookingService.findBookingsByUser("all", owner.getId(), 0, 10);
                });
    }
}
