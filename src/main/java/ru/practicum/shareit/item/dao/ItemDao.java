package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemDao {

    Item add(Item item);

    Item update(Item item);

    Item findById(Long id);

    List<Item> findAll(Long userId);

    List<Item> findByRequest(String text);
}