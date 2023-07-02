package ru.practicum.server.item;

import lombok.experimental.UtilityClass;
import ru.practicum.server.booking.dto.BookingDTO;
import ru.practicum.server.comment.dto.CommentDTO;
import ru.practicum.server.item.dto.ItemDTO;
import ru.practicum.server.item.dto.ItemShortDTO;
import ru.practicum.server.user.User;

import java.util.List;

@UtilityClass
public class ItemMapper {
    public static ItemDTO toItemWithCommentsDTO(Item item, List<CommentDTO> comments) {
        return ItemDTO.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(comments)
                .build();
    }

    public static ItemDTO toItemWithBookingDTO(Item item, BookingDTO lastBooking, BookingDTO nextBooking, List<CommentDTO> comments) {
        return ItemDTO.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();
    }

    public static Item toItem(ItemDTO itemDTO, User owner) {
        return Item.builder()
                .id(itemDTO.getId())
                .name(itemDTO.getName())
                .description(itemDTO.getDescription())
                .available(itemDTO.getAvailable())
                .owner(owner)
                .requestId(itemDTO.getRequestId())
                .build();
    }

    public static ItemDTO toItemDTO(Item item) {
        return ItemDTO.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequestId())
                .build();
    }

    public static ItemShortDTO toItemShortDTO(Item item) {
        return ItemShortDTO.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .ownerId(item.getOwner().getId())
                .available(item.getAvailable())
                .requestId(item.getRequestId())
                .build();
    }

}