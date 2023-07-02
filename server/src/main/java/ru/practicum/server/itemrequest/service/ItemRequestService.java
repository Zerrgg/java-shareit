package ru.practicum.server.itemrequest.service;

import ru.practicum.server.itemrequest.dto.ItemRequestDTO;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDTO createRequest(Long userId, ItemRequestDTO itemRequestDTO);

    ItemRequestDTO findById(Long requestId, Long userId);

    List<ItemRequestDTO> findAllByUser(Long userId);

    List<ItemRequestDTO> findAll(Long userId, int from, int size);

}
