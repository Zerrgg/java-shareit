package ru.practicum.shareit.item.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.OwnerNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    public static final String OWNER_NOT_FOUND = "Не найден владелец c id: ";

    private final ItemDao itemDao;
    private final UserDao userDao;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto add(ItemDto itemDto, Long userId) {
        boolean ownerExists = checkOwner(userId);
        if (!ownerExists) {
            throw new OwnerNotFoundException(OWNER_NOT_FOUND + userId);
        }
        Item item = itemMapper.toItem(itemDto, userId);
        item.setOwner(userId);
        return itemMapper.toItemDto(itemDao.add(item));
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long itemId, Long userId) {
        Item item = itemMapper.toItem(itemDto, userId);
        item.setId(itemId);
        return itemMapper.toItemDto(itemDao.update(item));
    }

    @Override
    public ItemDto findById(Long id) {
        return itemMapper.toItemDto(itemDao.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Не найдена вещь с id: " + id)));
    }

    @Override
    public List<ItemDto> findAllByUserId(Long userId) {
        userDao.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("не найден пользователь с id: " + userId));
        return itemDao.findAllByUserId(userId)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> findItemsByUserRequest(String text) {
        if (!StringUtils.hasLength(text)) {
            return new ArrayList<>();
        }
        return itemDao.findItemsByUserRequest(text)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private boolean checkOwner(Long ownerId) {
        List<User> users = userDao.findAll();
        List<User> result = users
                .stream()
                .filter(user -> user.getId().equals(ownerId))
                .collect(Collectors.toList());
        return result.size() > 0;
    }
}
