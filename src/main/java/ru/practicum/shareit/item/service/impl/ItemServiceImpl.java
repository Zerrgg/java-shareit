package ru.practicum.shareit.item.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.OwnerNotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    public static final String OWNER_NOT_FOUND_MESSAGE = "Не найден владелец c id: ";
    public static final int MIN_SEARCH_REQUEST_LENGTH = 3;

    private final ItemDao itemDao;
    private final UserDao userDao;

    @Override
    public Item add(Item item) {
        boolean ownerExists = checkOwner(item.getOwner());
        if (!ownerExists) {
            throw new OwnerNotFoundException(OWNER_NOT_FOUND_MESSAGE + item.getOwner());
        }
        return itemDao.add(item);
    }

    @Override
    public Item update(Item item) {
        return itemDao.update(item);
    }

    @Override
    public Item findById(Long id) {
        return itemDao.findById(id);
    }

    @Override
    public List<Item> findAll(Long userId) {
        return itemDao.findAll(userId);
    }

    @Override
    public List<Item> findByRequest(String text) {
        if (text == null || text.isBlank() || text.length() <= MIN_SEARCH_REQUEST_LENGTH) {
            return new ArrayList<>();
        }
        return itemDao.findByRequest(text);
    }

    private boolean checkOwner(Long ownerId) {
        List<User> users = userDao.findAll();
        List<User> result = users.stream().filter(user -> user.getId().equals(ownerId)).collect(Collectors.toList());
        return result.size() > 0;
    }
}
