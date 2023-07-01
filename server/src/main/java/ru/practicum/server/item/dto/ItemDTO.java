package ru.practicum.server.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.server.booking.dto.BookingDTO;
import ru.practicum.server.comment.dto.CommentDTO;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDTO lastBooking;
    private BookingDTO nextBooking;
    private List<CommentDTO> comments;
    private Long requestId;
}
