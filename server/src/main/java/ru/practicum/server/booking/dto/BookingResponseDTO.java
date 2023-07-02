package ru.practicum.server.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.server.booking.BookingStatus;
import ru.practicum.server.item.dto.ItemDTO;
import ru.practicum.server.user.dto.UserDTO;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDTO {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private UserDTO booker;
    private ItemDTO item;
}