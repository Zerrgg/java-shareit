package ru.practicum.shareit.item.dao.impl;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DeniedAccessException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Item update(Item item) {
        long itemId = item.getId();

        if (!items.containsKey(itemId)) {
            throw new ItemNotFoundException("Не найдена вещь с id: " + itemId);
        }

        Item updatedItem = items.get(itemId);

        if (!updatedItem.getOwner().equals(item.getOwner())) {
            throw new DeniedAccessException("Пользователь не является владельцем вещи" +
                    "userId: " + item.getOwner() + ", itemId: " + itemId);
        }

        updateItem(updatedItem, item);
        return updatedItem;
    }

    @Override
    public Item findById(Long id) {
        if (!items.containsKey(id)) {
            throw new ItemNotFoundException("Не найдена вещь с id: " + id);
        }
        return items.get(id);
    }

    @Override
    public List<Item> findAll(Long userId) {
        List<Item> result = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner().equals(userId)) result.add(item);
        }
        return result;
    }

    @Override
    public List<Item> findByRequest(String text) {
        List<Item> result = new ArrayList<>();
        String wantedItem = text.toLowerCase();

        for (Item item : items.values()) {
            String itemName = item.getName().toLowerCase();
            String itemDescription = item.getDescription().toLowerCase();

            if ((itemName.contains(wantedItem)
                    || itemDescription.contains(wantedItem))
                    && item.getAvailable().equals(true)) {
                result.add(item);
            }
        }
        return result;
    }

    private long generateId() {
        return currentId++;
    }

    private void updateItem(Item oldEntry, Item newEntry) {
        String name = newEntry.getName();
        if (name != null) {
            oldEntry.setName(name);
        }

        String description = newEntry.getDescription();
        if (description != null) {
            oldEntry.setDescription(description);
        }

        Boolean available = newEntry.getAvailable();
        if (available != null) {
            oldEntry.setAvailable(available);
        }
    }
}
