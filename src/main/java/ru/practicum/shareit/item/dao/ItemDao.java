package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.Item;

import java.util.List;
import java.util.Optional;

public interface ItemDao {

    Item add(Item item);

    Item update(Item item);

    Optional<Item> findById(Long id);

    List<Item> findAllByUserId(Long userId);

    List<Item> findItemsByUserRequest(String text);
}