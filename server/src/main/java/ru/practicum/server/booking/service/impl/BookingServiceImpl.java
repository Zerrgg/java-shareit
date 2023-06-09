package ru.practicum.server.booking.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.booking.Booking;
import ru.practicum.server.booking.BookingMapper;
import ru.practicum.server.booking.BookingStatus;
import ru.practicum.server.booking.State;
import ru.practicum.server.booking.dto.BookingDTO;
import ru.practicum.server.booking.dto.BookingResponseDTO;
import ru.practicum.server.booking.repository.BookingRepository;
import ru.practicum.server.booking.service.BookingService;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.exception.ValidateBookingException;
import ru.practicum.server.item.Item;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.user.User;
import ru.practicum.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

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

        Booking booking = BookingMapper.toBooking(bookingDTO, item, user);
        booking = bookingRepository.save(booking);
        return BookingMapper.toBookingResponseDTO(booking);
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

        BookingStatus status = Boolean.TRUE.equals(approved) ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        boolean trueStatus = booking.getStatus().equals(status);

        if (trueStatus) {
            log.warn("Уже выставлен статус-state: {}", status);
            throw new ValidateBookingException(String.format("Уже выставлен статус-state: %s", status));
        }

        booking.setStatus(status);
        booking = bookingRepository.save(booking);
        return BookingMapper.toBookingResponseDTO(booking);
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
        return BookingMapper.toBookingResponseDTO(booking);
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
        State bookings = State.valueOf(stateValue.toUpperCase());
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
        State bookings = State.valueOf(stateValue.toUpperCase());
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

    private List<BookingResponseDTO> getBookingByState(List<Booking> bookingList, State state) {
        LocalDateTime now = LocalDateTime.now();
        Stream<Booking> bookings = bookingList.stream();
        switch (state) {
            case REJECTED:
                bookings = bookings.filter(booking -> booking.getStatus().equals(BookingStatus.REJECTED));
                break;
            case WAITING:
                bookings = bookings.filter(booking -> booking.getStatus().equals(BookingStatus.WAITING));
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
