package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.dto.BookingPostDTO;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.DeniedAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnknownBookingException;
import ru.practicum.shareit.exception.ValidateBookingException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.booking.BookingStatus.REJECTED;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    public static final String STATE_LOG_MESSAGE = "Некорректно переданный статус-state: {}";

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    public BookingDTO createBooking(BookingPostDTO postDTO, Long userId) {

        boolean isStartBeforeEnd = postDTO.getStart().isBefore(postDTO.getEnd());

        if (!isStartBeforeEnd) {
            log.warn("Недопустимые значения времени start: {} и end: {}", postDTO.getStart(), postDTO.getEnd());
            throw new ValidateBookingException(
                    String.format("Недопустимые значения времени бронирования: start: %s, end: %s",
                            postDTO.getStart(), postDTO.getEnd()));
        }

        User user = checkUser(userId);
        Item item = checkItem(postDTO.getItemId());
        boolean trueOwner = item.getOwner().equals(userId);

        if (trueOwner) {
            log.warn("Владелец предмета не может забронировать свой предмет");
            throw new NotFoundException("Нельзя забронировать свой же предмет");
        }

        if (Boolean.FALSE.equals(item.getAvailable())) {
            log.warn("Бронирование не возможно");
            throw new ValidateBookingException(
                    String.format("В данный момент невозможно забронировать предмет: %d",
                            item.getId()));
        }

        Booking booking = BookingMapper.toBooking(postDTO, item, user);
        booking = bookingRepository.save(booking);
        return BookingMapper.toDTO(booking);
    }

    @Override
    public BookingDTO updateBooking(Long bookingId, Boolean approved, Long userId) {
        Booking booking = checkBooking(bookingId);
        Item item = checkItem(booking.getItem().getId());
        boolean trueOwner = item.getOwner().equals(userId);

        if (!trueOwner) {
            log.warn("Пользователь должен быть владельцем предмета");
            throw new NotFoundException(
                    String.format("Пользователь с Id: %d не является владельцем предмета c Id: %d ",
                            userId, item.getId()));
        }

        BookingStatus status = convertToStatus(approved);
        boolean trueStatus = booking.getStatus().equals(status);

        if (trueStatus) {
            log.warn("Уже выставлен статус-state: {}", status);
            throw new ValidateBookingException(String.format("Уже выставлен статус-state: %s", status));
        }

        booking.setStatus(status);
        booking = bookingRepository.save(booking);
        return BookingMapper.toDTO(booking);
    }

    @Override
    public BookingDTO findBookingById(Long bookingId, Long userId) {
        checkUser(userId);
        Booking booking = checkBooking(bookingId);
        Long itemOwner = booking.getItem().getOwner();
        Long bookingOwner = booking.getBooker().getId();
        boolean itemOrBookingOwner = userId.equals(bookingOwner) || userId.equals(itemOwner);

        if (!itemOrBookingOwner) {
            log.warn("Пользователь с Id: {} не является владельцем предмета c Id: {} или брони c Id: {}",
                    userId, booking.getItem().getId(), booking.getId());
            throw new NotFoundException(
                    String.format("Пользователь с Id: %d не является владельцем предмета c Id: %d или брони c Id: %d",
                            userId, booking.getItem().getId(), booking.getId()));
        }
        return BookingMapper.toDTO(booking);
    }

    @Override
    public List<BookingDTO> findAllByBooker(String stateValue, Long userId) {
        State state = parseState(stateValue);
        checkUser(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = new ArrayList<>();
        Sort sortDesc = Sort.by("start").descending();

        switch (state) {
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatus(userId, REJECTED, sortDesc);
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatus(userId, WAITING, sortDesc);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerIdCurrent(userId, now);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStartIsAfter(userId, now, sortDesc);
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndEndIsBefore(userId, now, sortDesc);
                break;
            case ALL:
                bookings = bookingRepository.findByBookerId(userId, sortDesc);
                break;
        }
        return BookingMapper.toListDTO(bookings);
    }

    @Override
    public List<BookingDTO> findAllByItemOwner(String stateValue, Long userId) {
        State state = parseState(stateValue);
        checkUser(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = new ArrayList<>();
        Sort sortDesc = Sort.by("start").descending();

        switch (state) {
            case REJECTED:
                bookings = bookingRepository.findBookingByItemOwnerAndStatus(userId, REJECTED, sortDesc);
                break;
            case WAITING:
                bookings = bookingRepository.findBookingByItemOwnerAndStatus(userId, WAITING, sortDesc);
                break;
            case CURRENT:
                bookings = bookingRepository.findBookingsByItemOwnerCurrent(userId, now);
                break;
            case FUTURE:
                bookings = bookingRepository.findBookingByItemOwnerAndStartIsAfter(userId, now, sortDesc);
                break;
            case PAST:
                bookings = bookingRepository.findBookingByItemOwnerAndEndIsBefore(userId, now, sortDesc);
                break;
            case ALL:
                bookings = bookingRepository.findBookingByItemOwner(userId, sortDesc);
                break;
        }
        return BookingMapper.toListDTO(bookings);
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
                    log.warn("Не найден пользователь с id-{}: ", userId);
                    return new NotFoundException(String.format(
                            "Не найден пользователь с id: %d", userId));
                }
        );
    }

    private Item checkItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> {
                    log.warn("Не найден предмет с id-{}: ", itemId);
                    return new NotFoundException(String.format(
                            "Не найден предмет с id: %d", itemId));
                }
        );
    }

    private Booking checkBooking(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> {
                    log.warn("Не найдено бронирование с id-{}: ", bookingId);
                    return new NotFoundException(String.format(
                            "Не найдено бронирование с id: %d", bookingId));
                }
        );
    }

    private State parseState(String stateValue) {
        try {
            return State.valueOf(stateValue);
        } catch (Exception e) {
            log.warn(STATE_LOG_MESSAGE, stateValue);
            throw new UnknownBookingException(String.format("Unknown state: %s", stateValue));
        }
    }

    private BookingStatus convertToStatus(Boolean approved) {
        return Boolean.TRUE.equals(approved) ? BookingStatus.APPROVED : REJECTED;
    }
}
