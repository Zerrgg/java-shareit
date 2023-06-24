package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.dto.BookingResponseDTO;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.impl.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnknownBookingException;
import ru.practicum.shareit.exception.ValidateBookingException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceTest {

    public static final LocalDateTime DATE = LocalDateTime.now();

    private BookingService bookingService;
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private BookingRepository bookingRepository;

    private User user;
    private Item item;
    private User owner;
    private Booking booking;
    private BookingDTO inputDto;

    @BeforeEach
    public void init() {
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        bookingRepository = mock(BookingRepository.class);
        bookingService = new BookingServiceImpl(userRepository, itemRepository, bookingRepository);

        inputDto = BookingDTO.builder()
                .id(1L)
                .itemId(5L)
                .start(DATE)
                .end(DATE.plusDays(10))
                .bookerId(2L)
                .status(BookingStatus.WAITING)
                .build();

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
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(DATE)
                .end(DATE.plusDays(10))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();
    }

    @Test
    void createBookingTest() {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any()))
                .thenReturn(booking);

        BookingResponseDTO result = bookingService.createBooking(inputDto, 2L);

        assertNotNull(result);
        assertEquals(inputDto.getItemId(), result.getItem().getId());
        assertEquals(inputDto.getStart(), result.getStart());
        assertEquals(inputDto.getEnd(), result.getEnd());
    }

    @Test
    void createBookingWithValidateExceptionTest() {
        item.setAvailable(false);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        Exception e = assertThrows(ValidateBookingException.class,
                () -> {
                    bookingService.createBooking(inputDto, 2L);
                });
        assertNotNull(e);
    }

    @Test
    void createBookingWithNotFoundExceptionTest() {
        item.setOwner(user);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        Exception e = assertThrows(NotFoundException.class,
                () -> {
                    bookingService.createBooking(inputDto, 2L);
                });
        assertNotNull(e);
    }

    @Test
    void updateBookingTest() {
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any()))
                .thenReturn(booking);

        BookingResponseDTO result = bookingService.updateBooking(1l, true, 3L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    void updateBookingWithNotFoundExceptionTest() {
        booking.setStatus(BookingStatus.WAITING);
        item.setOwner(user);

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        Exception e = assertThrows(NotFoundException.class,
                () -> {
                    bookingService.updateBooking(1l, true, 2l);
                });
        assertNotNull(e);
    }

    @Test
    void updateBookingWithValidateExceptionTest() {
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any()))
                .thenReturn(booking);

        booking.setStatus(BookingStatus.APPROVED);

        Exception e = assertThrows(ValidateBookingException.class,
                () -> {
                    bookingService.updateBooking(1L, true, 3L);
                });
        assertNotNull(e);
    }

    @Test
    void findBookingByIdTest() {
        item.setOwner(owner);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        BookingResponseDTO result = bookingService.findBookingById(1L, 2L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findBookingByIdWithNotFoundExceptionTest() {
        user.setId(11L);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        Exception e = assertThrows(NotFoundException.class,
                () -> {
                    bookingService.findBookingById(1L, 1L);
                });
        assertNotNull(e);
    }

    @Test
    void findBookingsByUserStateRejectedTest() {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        Exception e = assertThrows(NotFoundException.class,
                () -> {
                    List<BookingResponseDTO> result = bookingService
                            .findBookingsByUser("rejected", 2L, 0, 10);
                });
        assertNotNull(e);
    }

    @Test
    void findBookingsByUserStateWaitingTest() {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        Exception e = assertThrows(NotFoundException.class,
                () -> {
                    List<BookingResponseDTO> result = bookingService
                            .findBookingsByUser("waiting", 2L, 0, 10);
                });
        assertNotNull(e);
    }

    @Test
    void findBookingsByUserStateCurrentTest() {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        Exception e = assertThrows(NotFoundException.class,
                () -> {
                    List<BookingResponseDTO> result = bookingService
                            .findBookingsByUser("current", 2L, 0, 10);
                });
        assertNotNull(e);
    }

    @Test
    void findBookingsByUserStateFutureTest() {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        Exception e = assertThrows(NotFoundException.class,
                () -> {
                    bookingService.findBookingsByUser("future", 2L, 0, 10);
                });
        assertNotNull(e);
    }

    @Test
    void findBookingsByUserStatePastTest() {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        Exception e = assertThrows(NotFoundException.class,
                () -> {
                    List<BookingResponseDTO> result = bookingService
                            .findBookingsByUser("past", 2L, 0, 10);
                });
        assertNotNull(e);
    }

    @Test
    void findBookingsByUserStateAllTest() {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository.findByBooker(any(), any()))
                .thenReturn((List.of(booking)));

        List<BookingResponseDTO> result = bookingService
                .findBookingsByUser("all", 2L, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void findBookingsByUserUnknownStatusTest() {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository.findByBooker(any(), any()))
                .thenReturn(List.of(booking));

        Exception e = assertThrows(UnknownBookingException.class,
                () -> {
                    bookingService.findBookingsByUser("Unknown", 2L, 0, 10);
                });
        assertNotNull(e);
    }

    @Test
    void findBookingsByItemsOwnerStateRejectedTest() {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository.findByItemOwner(any(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingResponseDTO> result = bookingService
                .findBookingsByItemsOwner("rejected", 3L, 0, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findBookingsByItemsOwnerStateWaitingTest() {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository.findByItemOwner(any(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingResponseDTO> result = bookingService
                .findBookingsByItemsOwner("waiting", 3L, 0, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findBookingsByItemsOwnerStateCurrentTest() {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository.findByItemOwner(any(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingResponseDTO> result = bookingService
                .findBookingsByItemsOwner("current", 3L, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void findBookingsByItemsOwnerStateFutureTest() {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository.findByItemOwner(any(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingResponseDTO> result = bookingService
                .findBookingsByItemsOwner("future", 3L, 0, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findBookingsByItemsOwnerStatePastTest() {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository.findByItemOwner(any(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingResponseDTO> result = bookingService
                .findBookingsByItemsOwner("past", 3L, 0, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findBookingsByItemsOwnerStateAllTest() {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository.findByItemOwner(any(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingResponseDTO> result = bookingService
                .findBookingsByItemsOwner("all", 3L, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}