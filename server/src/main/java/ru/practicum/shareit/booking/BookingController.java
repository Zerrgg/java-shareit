package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.dto.BookingResponseDTO;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {

    public static final String DEFAULT_FROM_VALUE = "0";
    public static final String DEFAULT_SIZE_VALUE = "20";
    public static final String DEFAULT_STATE_VALUE = "ALL";
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDTO createBooking(@RequestBody BookingDTO bookingDTO,
                                            @RequestHeader(USER_ID_HEADER) Long userId,
                                            BindingResult result) {
        log.info("POST Запрос на создание бронирования пользователем с id-{}", userId);
        return bookingService.createBooking(bookingDTO, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDTO updateBooking(@PathVariable Long bookingId,
                                            @RequestParam Boolean approved,
                                            @RequestHeader(USER_ID_HEADER) Long userId,
                                            BindingResult result) {
        log.info("PATCH Запрос на обновление бронирования по id-{} пользователем c id-{}", bookingId, userId);
        return bookingService.updateBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDTO findBookingById(@PathVariable Long bookingId,
                                              @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("GET Запрос поиска по Id-{} бронирования и Id-{} пользователя", bookingId, userId);
        return bookingService.findBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingResponseDTO> findBookingsByUser(@RequestParam(defaultValue = DEFAULT_STATE_VALUE) String state,
                                                       @RequestHeader(USER_ID_HEADER) Long userId,
                                                       @RequestParam(defaultValue = DEFAULT_FROM_VALUE) int from,
                                                       @RequestParam(defaultValue = DEFAULT_SIZE_VALUE) int size) {
        log.info("GET Запрос на поиск брони пользователя c id-{} по заданному статусу-{}", userId, state);
        return bookingService.findBookingsByUser(state, userId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponseDTO> findBookingsByItemsOwner(@RequestParam(defaultValue = DEFAULT_STATE_VALUE) String state,
                                                             @RequestHeader(USER_ID_HEADER) Long userId,
                                                             @RequestParam(defaultValue = DEFAULT_FROM_VALUE) int from,
                                                             @RequestParam(defaultValue = DEFAULT_SIZE_VALUE) int size) {
        log.info("GET Запрос на поиск забронированных вещей" +
                "одного владельца по его Id-{} и заданному статусу-{} бронирования", userId, state);
        return bookingService.findBookingsByItemsOwner(state, userId, from, size);
    }
}
