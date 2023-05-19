package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.markers.Create;
import ru.practicum.shareit.exception.markers.Update;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {

    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    public static final int MIN_ID_VALUE = 1;
    public static final String NULL_ITEM_ID_MESSAGE = "itemID is null";
    public static final String NULL_USER_ID_MESSAGE = "userID is null";

    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @PostMapping
    public ItemDto add(@Validated({Create.class})
                       @RequestBody ItemDto itemDto,
                       @NotNull(message = (NULL_ITEM_ID_MESSAGE))
                       @Min(MIN_ID_VALUE)
                       @RequestHeader(USER_ID_HEADER) Long userId) {
        Item item = itemMapper.intoModel(itemDto, userId);
        return itemMapper.intoDto(itemService.add(item));
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@Validated({Update.class})
                          @RequestBody ItemDto itemDto,
                          @NotNull(message = NULL_ITEM_ID_MESSAGE)
                          @Min(MIN_ID_VALUE)
                          @PathVariable Long itemId,
                          @NotNull(message = NULL_USER_ID_MESSAGE)
                          @Min(MIN_ID_VALUE)
                          @RequestHeader(USER_ID_HEADER) Long userId) {
        Item item = itemMapper.intoModel(itemDto, userId);
        item.setId(itemId);
        return itemMapper.intoDto(itemService.update(item));
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@NotNull(message = NULL_ITEM_ID_MESSAGE)
                            @Min(MIN_ID_VALUE)
                            @PathVariable Long itemId) {
        return itemMapper.intoDto(itemService.findById(itemId));
    }

    @GetMapping
    public List<ItemDto> findAll(@NotNull(message = NULL_USER_ID_MESSAGE)
                                      @Min(MIN_ID_VALUE)
                                      @RequestHeader(USER_ID_HEADER) Long userId) {
        List<Item> userItems = itemService.findAll(userId);
        return itemMapper.intoDtoList(userItems);
    }

    @GetMapping("/search")
    public List<ItemDto> findByRequest(@RequestParam String text) {
        List<Item> foundItems = itemService.findByRequest(text);
        return itemMapper.intoDtoList(foundItems);
    }
}
