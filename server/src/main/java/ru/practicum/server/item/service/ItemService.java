package ru.practicum.server.item.service;

import ru.practicum.server.comment.dto.CommentDTO;
import ru.practicum.server.item.dto.ItemDTO;

import java.util.List;

public interface ItemService {

    ItemDTO createItem(ItemDTO itemDTO, Long userId);

    ItemDTO updateItem(ItemDTO itemDTO, Long itemId, Long userId);

    ItemDTO findItemById(Long itemId, Long userId);

    List<ItemDTO> findAllItemsByUserId(Long userId, int from, int size);

    List<ItemDTO> findItemsByRequest(String text, int from, int size);

    CommentDTO addComment(CommentDTO commentDTO, Long itemId, Long userId);
}
