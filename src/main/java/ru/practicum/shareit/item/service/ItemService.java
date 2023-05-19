package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemService {

    Item add(Item item);

    Item update(Item item);

    Item findById(Long id);

    List<Item> findAll(Long userId);

    List<Item> findByRequest(String text);
}
