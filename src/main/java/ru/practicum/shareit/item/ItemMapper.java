package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDTO;

import java.util.List;

@UtilityClass
public class ItemMapper {
    public ItemDTO toItemDTO(Item item, List<Comment> comments) {
        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setId(item.getId());
        itemDTO.setName(item.getName());
        itemDTO.setDescription(item.getDescription());
        itemDTO.setAvailable(item.getAvailable());
        if (comments != null) {
            itemDTO.setComments(CommentMapper.toCommentDtoList(comments));
        }
        return itemDTO;
    }

    public ItemDTO toItemDTO(Item item, Booking lastBooking, Booking nextBooking, List<Comment> comments) {
        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setId(item.getId());
        itemDTO.setName(item.getName());
        itemDTO.setDescription(item.getDescription());
        itemDTO.setAvailable(item.getAvailable());
        itemDTO.setLastBooking(BookingMapper.bookingItemDTO(lastBooking));
        itemDTO.setNextBooking(BookingMapper.bookingItemDTO(nextBooking));
        if (comments != null) {
            itemDTO.setComments(CommentMapper.toCommentDtoList(comments));
        }
        return itemDTO;
    }

    public Item toItem(ItemDTO itemDTO, Long ownerId) {
        return new Item(
                itemDTO.getId(),
                itemDTO.getName(),
                itemDTO.getDescription(),
                itemDTO.getAvailable(),
                ownerId);
    }

}