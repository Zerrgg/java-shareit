package ru.practicum.server.itemrequest;

import lombok.experimental.UtilityClass;
import ru.practicum.server.itemrequest.dto.ItemRequestDTO;

import java.util.ArrayList;

@UtilityClass
public class ItemRequestMapper {
    public static ItemRequest toItemRequest(ItemRequestDTO itemRequestDTO) {
        return ItemRequest.builder()
                .id(itemRequestDTO.getId())
                .description(itemRequestDTO.getDescription())
                .created(itemRequestDTO.getCreated())
                .requestorId(itemRequestDTO.getRequestorId())
                .build();
    }

    public static ItemRequestDTO toItemRequestDTO(ItemRequest itemRequest) {
        return ItemRequestDTO.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .items(new ArrayList<>())
                .created(itemRequest.getCreated())
                .requestorId(itemRequest.getRequestorId())
                .build();
    }
}
