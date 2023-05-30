package ru.practicum.shareit.item.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.NotFoundException;
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

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    public static final String OWNER_NOT_FOUND_MESSAGE = "Не найден владелец c id: ";

    private final ItemDao itemDao;
    private final UserDao userDao;

    @Override
    public ItemDto add(ItemDto itemDto, Long userId) {
        boolean ownerExists = checkOwner(userId);
        if (!ownerExists) {
            log.warn("Не найден владелец c id-{}: ", userId);
            throw new NotFoundException(OWNER_NOT_FOUND_MESSAGE + userId);
        }
        Item item = ItemMapper.toItem(itemDto, userId);
        item.setOwner(userId);
        return ItemMapper.toItemDto(itemDao.add(item));
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long itemId, Long userId) {
        Item item = ItemMapper.toItem(itemDto, userId);
        item.setId(itemId);
        return ItemMapper.toItemDto(itemDao.update(item));
    }

    @Override
    public ItemDto findById(Long id) {
        Item item = itemDao.findById(id)
                .orElseThrow(() -> {
                            log.warn("Не найдена вещь с id-{}: ", id);
                            return new NotFoundException(String.format(
                                    "Не найдена вещь с id: %d", id));
                        }
                );
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> findAllByUserId(Long userId) {
        userDao.findById(userId)
                .orElseThrow(() -> {
                            log.warn("Не найден пользователь с id: {}", userId);
                            return new NotFoundException(String.format("Не найден пользователь с id: %d", userId));
                        }
                );
        return itemDao.findAllByUserId(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> findItemsByUserRequest(String text) {
        if (!StringUtils.hasLength(text)) {
            return new ArrayList<>();
        }
        return itemDao.findItemsByUserRequest(text)
                .stream()
                .map(ItemMapper::toItemDto)
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
