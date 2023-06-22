package ru.practicum.shareit.itemrequest.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.itemrequest.ItemRequest;
import ru.practicum.shareit.itemrequest.ItemRequestMapper;
import ru.practicum.shareit.itemrequest.dto.ItemRequestDTO;
import ru.practicum.shareit.itemrequest.repository.ItemRequestRepository;
import ru.practicum.shareit.itemrequest.service.ItemRequestService;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.itemrequest.ItemRequestMapper.toItemRequest;
import static ru.practicum.shareit.itemrequest.ItemRequestMapper.toItemRequestDTO;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public ItemRequestDTO createRequest(Long userId, ItemRequestDTO itemRequestDto) {
        User user = checkUser(userId);
        ItemRequest itemRequest = toItemRequest(itemRequestDto);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(user);
        itemRequestRepository.save(itemRequest);

        return toItemRequestDTO(itemRequest);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequestDTO findById(Long requestId, Long userId) {
        checkUser(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Невозможно найти запрос - " +
                        "не существует запроса с id " + requestId));
        ItemRequestDTO itemRequestDto = toItemRequestDTO(itemRequest);

        return setItemsToItemRequestDto(itemRequestDto);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDTO> findAllByUser(Long userId) {
        checkUser(userId);
        List<ItemRequest> list = itemRequestRepository.findAllByRequestorIdOrderByCreatedAsc(userId);
        return toDtoList(list);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDTO> findAll(Long userId, int from, int size) {
        User user = checkUser(userId);
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        List<ItemRequest> list = itemRequestRepository.findAllByRequestorIsNot(user, pageRequest);
        return toDtoList(list);
    }

    private ItemRequestDTO setItemsToItemRequestDto(ItemRequestDTO itemRequestDto) {
        itemRequestDto.setItems(itemRepository.findAllByRequestId(itemRequestDto.getId())
                .stream()
                .map(ItemMapper::toItemShortDTO)
                .collect(Collectors.toList()));
        return itemRequestDto;
    }

    private List<ItemRequestDTO> toDtoList(List<ItemRequest> list) {
        return list
                .stream()
                .map(ItemRequestMapper::toItemRequestDTO)
                .map(this::setItemsToItemRequestDto)
                .collect(Collectors.toList());
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
                    log.warn("Не найден пользователь с id-{}: ", userId);
                    return new NotFoundException(String.format(
                            "Не найден пользователь с id: %d", userId));
                }
        );
    }
}