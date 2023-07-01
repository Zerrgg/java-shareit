package ru.practicum.server.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.server.booking.dto.BookingDTO;
import ru.practicum.server.booking.dto.BookingResponseDTO;
import ru.practicum.server.item.Item;
import ru.practicum.server.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BookingMapperTest {

    public static final LocalDateTime DATE = LocalDateTime.now();

    private User user;
    private Item item;
    private Booking booking;
    private BookingDTO bookingDto;

    @BeforeEach
    public void inith() {
        bookingDto = new BookingDTO(1L, 3L, DATE, DATE.plusDays(7), 5L, BookingStatus.WAITING);
        user = new User(5L, "name", "user@emali.com");
        item = new Item(3L, "name", "description", true, user, null);
        booking = new Booking(2L, DATE, DATE.plusDays(7), item, user, BookingStatus.APPROVED);
    }

    @Test
    void toBookingTest() {
        Booking result = BookingMapper.toBooking(bookingDto, item, user);

        assertNotNull(result);
        assertEquals(bookingDto.getStart(), result.getStart());
        assertEquals(bookingDto.getEnd(), result.getEnd());
        assertEquals(item.getId(), result.getItem().getId());
        assertEquals(user.getId(), result.getBooker().getId());
        assertEquals(bookingDto.getId(), result.getId());
        assertEquals(bookingDto.getStatus(), result.getStatus());
    }

    @Test
    void toBookingResponseDT0Test() {
        BookingResponseDTO result = BookingMapper.toBookingResponseDTO(booking);

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getStatus(), result.getStatus());
        assertEquals(user.getId(), result.getBooker().getId());
        assertEquals(item.getId(), result.getItem().getId());
    }

    @Test
    void toBookingDtoTest() {
        BookingDTO result = BookingMapper.toBookingDto(booking);

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getItem().getId(), result.getItemId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getBooker().getId(), result.getBookerId());
        assertEquals(booking.getStatus(), result.getStatus());
    }
}