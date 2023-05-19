package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.markers.Create;
import ru.practicum.shareit.exception.markers.Update;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {

    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemService itemService;

    @PostMapping
    public ItemDto add(@Validated({Create.class})
                       @RequestBody ItemDto itemDto,
                       @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemService.add(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@Validated({Update.class})
                          @RequestBody ItemDto itemDto,
                          @PathVariable Long itemId,
                          @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemService.update(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@PathVariable Long itemId) {
        return itemService.findById(itemId);
    }

    @GetMapping
    public List<ItemDto> findAllByUserId(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemService.findAllByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> findByRequest(@RequestParam String text) {
        return itemService.findItemsByUserRequest(text);
    }
}
