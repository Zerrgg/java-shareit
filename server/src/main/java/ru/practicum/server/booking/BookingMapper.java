package ru.practicum.server.booking;

import lombok.experimental.UtilityClass;
import ru.practicum.server.booking.dto.BookingDTO;
import ru.practicum.server.booking.dto.BookingResponseDTO;
import ru.practicum.server.item.Item;
import ru.practicum.server.item.ItemMapper;
import ru.practicum.server.user.User;
import ru.practicum.server.user.UserMapper;

@UtilityClass
public class BookingMapper {

    public static BookingResponseDTO toBookingResponseDTO(Booking booking) {
        return BookingResponseDTO.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(UserMapper.toUserDTO(booking.getBooker()))
                .item(ItemMapper.toItemDTO(booking.getItem()))
                .build();
    }

    public static Booking toBooking(BookingDTO bookingDTO, Item item, User booker) {
        return Booking.builder()
                .id(bookingDTO.getId())
                .start(bookingDTO.getStart())
                .end(bookingDTO.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
    }

    public BookingDTO toBookingDto(Booking booking) {
        return BookingDTO.builder()
                .id(booking.getId())
                .itemId(booking.getItem().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(booking.getBooker().getId())
                .status(booking.getStatus())
                .build();
    }

}
