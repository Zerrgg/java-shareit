package ru.practicum.server.itemrequest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.server.item.dto.ItemShortDTO;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDTO {
    private Long id;
    private String description;
    private List<ItemShortDTO> items;
    private LocalDateTime created;
    private Long requestorId;
}
