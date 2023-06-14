package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDTO;
import ru.practicum.shareit.exception.markers.Create;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemService itemService;

    @PostMapping
    public ItemDTO createItem(@Validated({Create.class})
                              @RequestBody ItemDTO itemDto,
                              @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("POST Запрос на добавление пользователем с id-{} предмета {}", userId, itemDto);
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDTO updateItem(@RequestBody ItemDTO itemDto,
                              @PathVariable Long itemId,
                              @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("PATCH Запрос на обновление предмета по id-{} пользователем c id-{}", itemId, userId);
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDTO findItemById(@PathVariable Long itemId,
                                @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("GET Запрос поиска предмета-{} пользователя c id-{} ", itemId, userId);
        return itemService.findItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDTO> findAllItemByUserId(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("GET Запрос на поиск предметов пользователя c id-{}", userId);
        return itemService.findAllItemsByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDTO> findItemByRequest(@RequestParam String text,
                                           @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("GET Запрос на поиск предметов по запросу-{} от пользователя-{}", text, userId);
        return itemService.findItemsByRequest(text, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDTO addComment(@Validated(Create.class)
                                 @RequestBody CommentDTO commentDTO,
                                 @PathVariable Long itemId,
                                 @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("POST Запрос от пользователя с id-{} на добавление комментария-{} к предмету с i-{}", userId, commentDTO, itemId);
        return itemService.addComment(commentDTO, itemId, userId);
    }
}
