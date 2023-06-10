package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingResponseDTO;
import ru.practicum.shareit.booking.dto.BookingDTO;

import java.util.List;

public interface BookingService {

    BookingResponseDTO createBooking(BookingDTO postDTO, Long userId);

    BookingResponseDTO updateBooking(Long bookingId, Boolean approved, Long userId);

    BookingResponseDTO findBookingById(Long bookingId, Long userId);

    List<BookingResponseDTO> findBookingsByUser(String state, Long userId);

    List<BookingResponseDTO> findBookingsByItemsOwner(String state, Long userId);

}
