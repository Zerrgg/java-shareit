package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class BookingMapper {

    public BookingDTO toDTO(Booking booking) {
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setId(booking.getId());
        bookingDTO.setStart(booking.getStart());
        bookingDTO.setEnd(booking.getEnd());
        bookingDTO.setStatus(booking.getStatus());
        bookingDTO.setBooker(booking.getBooker());
        bookingDTO.setItem(booking.getItem());
        bookingDTO.setName(booking.getItem().getName());
        return bookingDTO;
    }

    public Booking toBooking(BookingPostDTO postDTO, Item item, User user) {
        Booking booking = new Booking();
        booking.setStart(postDTO.getStart());
        booking.setEnd(postDTO.getEnd());
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    public BookingItemDTO bookingItemDTO(Booking booking) {
        if (booking == null) return null;

        BookingItemDTO bookingItemDTO = new BookingItemDTO();
        bookingItemDTO.setId(booking.getId());
        bookingItemDTO.setBookerId(booking.getBooker().getId());
        bookingItemDTO.setStart(booking.getStart());
        bookingItemDTO.setEnd(booking.getEnd());
        return bookingItemDTO;
    }

    public BookingResponseDTO toResponseDTO(Booking booking, User booker, Item item) {
        BookingResponseDTO bookingResponseDTO = new BookingResponseDTO();
        bookingResponseDTO.setId(booking.getId());
        bookingResponseDTO.setStatus(booking.getStatus());
        bookingResponseDTO.setBooker(booker);
        bookingResponseDTO.setItem(item);
        bookingResponseDTO.setName(item.getName());
        return bookingResponseDTO;
    }

    public List<BookingDTO> toListDTO(List<Booking> bookings) {
        return bookings
                .stream()
                .map(BookingMapper::toDTO)
                .collect(Collectors.toList());
    }
}
