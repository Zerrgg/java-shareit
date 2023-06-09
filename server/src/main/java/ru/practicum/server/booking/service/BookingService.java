package ru.practicum.server.booking.service;

import ru.practicum.server.booking.dto.BookingDTO;
import ru.practicum.server.booking.dto.BookingResponseDTO;

import java.util.List;

public interface BookingService {

    BookingResponseDTO createBooking(BookingDTO postDTO, Long userId);

    BookingResponseDTO updateBooking(Long bookingId, Boolean approved, Long userId);

    BookingResponseDTO findBookingById(Long bookingId, Long userId);

    List<BookingResponseDTO> findBookingsByUser(String state, Long userId, int from, int size);

    List<BookingResponseDTO> findBookingsByItemsOwner(String state, Long userId, int from, int size);

}
