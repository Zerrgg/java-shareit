package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDTO;

import java.util.List;

public interface ItemService {

    ItemDTO createItem(ItemDTO itemDTO, Long userId);

    ItemDTO updateItem(ItemDTO itemDTO, Long itemId, Long userId);

    ItemDTO findItemById(Long itemId, Long userId);

    List<ItemDTO> findAllItemsByUserId(Long userId);

    List<ItemDTO> findItemsByRequest(String text, Long userId);

    CommentDTO addComment(CommentDTO commentDTO, Long itemId, Long userId);
}
