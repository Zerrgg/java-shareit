package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDTO;

import java.util.List;

public interface ItemService {

    ItemDTO add(ItemDTO itemDto, Long userId);

    ItemDTO update(ItemDTO itemDto, Long itemId, Long userId);

    ItemDTO findById(Long id);

    List<ItemDTO> findAllByUserId(Long userId);

    List<ItemDTO> findItemsByUserRequest(String text);
}
