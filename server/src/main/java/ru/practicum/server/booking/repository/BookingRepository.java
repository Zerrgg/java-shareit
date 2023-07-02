package ru.practicum.server.booking.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.server.booking.Booking;
import ru.practicum.server.booking.BookingStatus;
import ru.practicum.server.item.Item;
import ru.practicum.server.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByItemOwner(User owner, PageRequest pageRequest);

    List<Booking> findByBooker(User user, PageRequest pageRequest);

    List<Booking> findAllByItemAndStatusOrderByStartAsc(Item item, BookingStatus status);

    List<Booking> findAllByItemInAndStatusOrderByStartAsc(List<Item> items, BookingStatus status);

    boolean existsBookingByItemAndBookerAndStatusNotAndStartBefore(Item item, User booker,
                                                                   BookingStatus status, LocalDateTime time);
}

