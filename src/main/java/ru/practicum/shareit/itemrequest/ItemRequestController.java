package ru.practicum.shareit.itemrequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.markers.Create;
import ru.practicum.shareit.itemrequest.dto.ItemRequestDTO;
import ru.practicum.shareit.itemrequest.service.ItemRequestService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class ItemRequestController {

    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDTO createRequest(@Positive
                                        @RequestHeader(USER_ID_HEADER) Long userId,
                                        @Validated({Create.class})
                                        @RequestBody ItemRequestDTO itemRequestDTO) {
        log.info("POST запрос к эндпоинту /requests create с headers {}", userId);
        return itemRequestService.createRequest(userId, itemRequestDTO);
    }

    @GetMapping
    public List<ItemRequestDTO> findAllByUser(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("GET запрос к эндпоинту /requests findAllByUser с headers {}", userId);
        return itemRequestService.findAllByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDTO> findAll(@RequestHeader(USER_ID_HEADER) Long userId,
                                        @PositiveOrZero
                                        @RequestParam(defaultValue = "0") int from,
                                        @Positive
                                        @RequestParam(defaultValue = "10") int size) {
        log.info("GET запрос к эндпоинту /requests findAll с headers {}, from{}, size{}", userId, from, size);
        return itemRequestService.findAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDTO findById(@PathVariable Long requestId,
                                   @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("GET запрос к эндпоинту /requests findById с headers {}, c requestId {}", userId, requestId);
        return itemRequestService.findById(requestId, userId);
    }
}
