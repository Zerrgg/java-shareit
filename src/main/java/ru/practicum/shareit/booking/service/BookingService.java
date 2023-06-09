package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.dto.BookingPostDTO;

import java.util.List;

public interface BookingService {

    BookingDTO createBooking(BookingPostDTO postDTO, Long userId);

    BookingDTO updateBooking(Long bookingId, Boolean approved, Long userId);

    BookingDTO findBookingById(Long bookingId, Long userId);

    List<BookingDTO> findAllByBooker(String state, Long userId);

    List<BookingDTO> findAllByItemOwner(String state, Long userId);

}
