package ru.practicum.server.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.server.booking.BookingStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
    private Long id;
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long bookerId;
    private BookingStatus status;
}
