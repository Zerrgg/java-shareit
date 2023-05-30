package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDTO;

@UtilityClass
public class ItemMapper {
    public static ItemDTO toItemDto(Item item) {
        return new ItemDTO(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId() != null ? item.getRequestId() : null);
    }

    public static Item toItem(ItemDTO itemDto, Long ownerId) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                ownerId,
                itemDto.getRequestId() != null ? itemDto.getRequestId() : null);
    }
}