package ru.practicum.server.itemrequest.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.item.ItemMapper;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.itemrequest.ItemRequest;
import ru.practicum.server.itemrequest.ItemRequestMapper;
import ru.practicum.server.itemrequest.dto.ItemRequestDTO;
import ru.practicum.server.itemrequest.repository.ItemRequestRepository;
import ru.practicum.server.itemrequest.service.ItemRequestService;
import ru.practicum.server.user.User;
import ru.practicum.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
        checkUser(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestorId(userId);
        ItemRequest request = itemRequestRepository.save(itemRequest);

        return ItemRequestMapper.toItemRequestDTO(request);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequestDTO findById(Long requestId, Long userId) {
        checkUser(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Невозможно найти запрос - " +
                        "не существует запроса с id " + requestId));
        ItemRequestDTO itemRequestDto = ItemRequestMapper.toItemRequestDTO(itemRequest);

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
        checkUser(userId);
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        List<ItemRequest> list = itemRequestRepository.findAllByRequestorIdIsNot(userId, pageRequest);
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