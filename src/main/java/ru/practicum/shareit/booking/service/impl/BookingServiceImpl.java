package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.dto.BookingResponseDTO;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnknownBookingException;
import ru.practicum.shareit.exception.ValidateBookingException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.booking.BookingMapper.toBooking;
import static ru.practicum.shareit.booking.BookingMapper.toBookingResponseDTO;
import static ru.practicum.shareit.booking.BookingStatus.REJECTED;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public BookingResponseDTO createBooking(BookingDTO bookingDTO, Long userId) {
        User user = checkUser(userId);
        Item item = checkItem(bookingDTO.getItemId());

        boolean isStartBeforeEnd = bookingDTO.getStart().isBefore(bookingDTO.getEnd());

        if (!isStartBeforeEnd) {
            log.warn("Недопустимые значения времени start: {} и end: {}", bookingDTO.getStart(), bookingDTO.getEnd());
            throw new ValidateBookingException(
                    String.format("Недопустимые значения времени бронирования: start: %s, end: %s",
                            bookingDTO.getStart(), bookingDTO.getEnd()));
        }

        boolean trueOwner = item.getOwner().getId().equals(userId);

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

        Booking booking = toBooking(bookingDTO, item, user);
        booking = bookingRepository.save(booking);
        return toBookingResponseDTO(booking);
    }

    @Override
    @Transactional
    public BookingResponseDTO updateBooking(Long bookingId, Boolean approved, Long userId) {
        Booking booking = checkBooking(bookingId);
        Item item = checkItem(booking.getItem().getId());
        boolean trueOwner = item.getOwner().getId().equals(userId);

        if (!trueOwner) {
            log.warn("Пользователь должен быть владельцем предмета");
            throw new NotFoundException(
                    String.format("Пользователь с Id: %d не является владельцем предмета c Id: %d ",
                            userId, item.getId()));
        }

        BookingStatus status = Boolean.TRUE.equals(approved) ? BookingStatus.APPROVED : REJECTED;
        boolean trueStatus = booking.getStatus().equals(status);

        if (trueStatus) {
            log.warn("Уже выставлен статус-state: {}", status);
            throw new ValidateBookingException(String.format("Уже выставлен статус-state: %s", status));
        }

        booking.setStatus(status);
        booking = bookingRepository.save(booking);
        return toBookingResponseDTO(booking);
    }

    @Override
    @Transactional
    public BookingResponseDTO findBookingById(Long bookingId, Long userId) {
        checkUser(userId);
        Booking booking = checkBooking(bookingId);
        Long itemOwner = booking.getItem().getOwner().getId();
        Long bookingOwner = booking.getBooker().getId();
        boolean itemOrBookingOwner = userId.equals(bookingOwner) || userId.equals(itemOwner);

        if (!itemOrBookingOwner) {
            log.warn("Пользователь с Id: {} не является владельцем предмета c Id: {} или брони c Id: {}",
                    userId, booking.getItem().getId(), booking.getId());
            throw new NotFoundException(
                    String.format("Пользователь с Id: %d не является владельцем предмета c Id: %d или брони c Id: %d",
                            userId, booking.getItem().getId(), booking.getId()));
        }
        return toBookingResponseDTO(booking);
    }

    @Override
    @Transactional
    public List<BookingResponseDTO> findBookingsByUser(String stateValue, Long userId, int from, int size) {
        User owner = checkUser(userId);
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by("start").descending());
        List<Booking> ownerBookings = bookingRepository.findByBooker(owner, pageRequest);
        if (ownerBookings.isEmpty()) {
            throw new NotFoundException("Бронирование не найдено");
        }
        State bookings = parseState(stateValue);
        return getBookingByState(ownerBookings, bookings);
    }

    @Override
    @Transactional
    public List<BookingResponseDTO> findBookingsByItemsOwner(String stateValue, Long userId, int from, int size) {
        User owner = checkUser(userId);
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by("start").descending());
        List<Booking> ownerBookings = bookingRepository.findByItemOwner(owner, pageRequest);
        if (ownerBookings.isEmpty()) {
            throw new NotFoundException("Бронирование не найдено");
        }
        State bookings = parseState(stateValue);
        return getBookingByState(ownerBookings, bookings);
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
            return State.valueOf(stateValue.toUpperCase());
        } catch (Exception e) {
            log.warn("Некорректно переданный статус-state: {}", stateValue);
            throw new UnknownBookingException(String.format("Unknown state: %s", stateValue));
        }
    }

    private List<BookingResponseDTO> getBookingByState(List<Booking> bookingList, State state) {
        LocalDateTime now = LocalDateTime.now();
        Stream<Booking> bookings = bookingList.stream();
        switch (state) {
            case REJECTED:
                bookings = bookings.filter(booking -> booking.getStatus().equals(REJECTED));
                break;
            case WAITING:
                bookings = bookings.filter(booking -> booking.getStatus().equals(WAITING));
                break;
            case CURRENT:
                bookings = bookings.filter(booking -> booking.getStart().isBefore(now) &&
                        booking.getEnd().isAfter(now));
                break;
            case FUTURE:
                bookings = bookings.filter(booking -> booking.getStart().isAfter(now));
                break;
            case PAST:
                bookings = bookings.filter(booking -> booking.getEnd().isBefore(now));
                break;
        }

        List<Booking> sortedBookings = bookings
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .collect(toList());

        return sortedBookings
                .stream()
                .map(BookingMapper::toBookingResponseDTO)
                .collect(toList());

    }

}
