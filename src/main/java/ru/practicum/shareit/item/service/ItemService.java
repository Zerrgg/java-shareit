package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto add(ItemDto itemDto, Long userId);

    ItemDto update(ItemDto itemDto, Long itemId, Long userId);

    ItemDto findById(Long id);

    List<ItemDto> findAllByUserId(Long userId);

    List<ItemDto> findItemsByUserRequest(String text);
}
