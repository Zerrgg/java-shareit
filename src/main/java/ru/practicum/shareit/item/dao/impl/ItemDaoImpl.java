package ru.practicum.shareit.item.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DeniedAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.Item;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ItemDaoImpl implements ItemDao {

    private final Map<Long, Item> items = new HashMap<>();
    private long currentId = 1;

    @Override
    public Item add(Item item) {
        long id = generateId();
        item.setId(id);
        items.put(id, item);
        return item;
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> findAllByUserId(Long userId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwner().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Item update(Item item) {

        if (!items.containsKey(item.getId())) {
            log.warn("Не найдена вещь с id: {}", item.getId());
            throw new NotFoundException(String.format(
                    "Не найдена вещь с id: %d", item.getId()));
        }
        Item updatedItem = items.get(item.getId());

        if (!updatedItem.getOwner().equals(item.getOwner())) {
            log.warn("Пользователь не является владельцем вещи userId: {} , itemId: {}", item.getOwner(), item.getId());
            throw new DeniedAccessException(String.format(
                    "Пользователь не является владельцем вещи userId: %d, itemId: %d", item.getOwner(), item.getId()));
        }
        updateItem(updatedItem, item);
        return updatedItem;
    }

    @Override
    public List<Item> findItemsByUserRequest(String text) {
        String wantedItem = text.toLowerCase();
        return items.values()
                .stream()
                .filter(item -> item.getAvailable().equals(true) && (item.getName().toLowerCase().contains(wantedItem) ||
                        item.getDescription().toLowerCase().contains(wantedItem)))
                .collect(Collectors.toList());
    }

    private Long generateId() {
        return currentId++;
    }

    private void updateItem(Item oldEntry, Item newEntry) {
        String name = newEntry.getName();
        if (name != null)
            oldEntry.setName(name);

        String description = newEntry.getDescription();
        if (description != null)
            oldEntry.setDescription(description);

        Boolean available = newEntry.getAvailable();
        if (available != null)
            oldEntry.setAvailable(available);
    }
}
