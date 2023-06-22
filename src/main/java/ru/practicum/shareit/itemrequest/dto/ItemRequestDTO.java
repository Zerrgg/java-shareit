package ru.practicum.shareit.itemrequest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.exception.markers.Create;
import ru.practicum.shareit.item.dto.ItemShortDTO;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDTO {
    private Long id;
    @NotBlank(groups = {Create.class})
    private String description;
    private List<ItemShortDTO> items;
    private LocalDateTime created;
    private Long requestorId;
}
