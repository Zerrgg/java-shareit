package ru.practicum.shareit.itemrequest;

import ru.practicum.shareit.itemrequest.dto.ItemRequestDTO;

import java.util.ArrayList;

public class ItemRequestMapper {
    public static ItemRequest toItemRequest(ItemRequestDTO itemRequestDTO) {
        return ItemRequest.builder()
                .id(itemRequestDTO.getId())
                .description(itemRequestDTO.getDescription())
                .created(itemRequestDTO.getCreated())
                .build();
    }

    public static ItemRequestDTO toItemRequestDTO(ItemRequest itemRequest) {
        return ItemRequestDTO.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .items(new ArrayList<>())
                .created(itemRequest.getCreated())
                .build();
    }
}
