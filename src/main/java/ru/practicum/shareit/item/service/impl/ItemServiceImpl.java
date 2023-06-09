package ru.practicum.shareit.item.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.dto.AddCommentDTO;
import ru.practicum.shareit.comment.dto.CommentDTO;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.ValidateCommentException;
import ru.practicum.shareit.exception.DeniedAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor

public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDTO createItem(ItemDTO itemDTO, Long userId) {
        Long ownerId = checkUser(userId).getId();
        Item item = ItemMapper.toItem(itemDTO, ownerId);
        return ItemMapper.toItemDTO(itemRepository.save(item), null);
    }

    @Override
    public ItemDTO updateItem(ItemDTO itemDTO, Long itemId, Long userId) {
        checkUser(userId);
        Item item = checkItem(itemId);
        boolean trueOwner = item.getOwner().equals(userId);

        if (!trueOwner) {
            log.warn("Пользователь должен быть владельцем предмета");
            throw new DeniedAccessException(
                    String.format("Пользователь с Id: %d не является владельцем предмета c Id: %d ",
                            userId, item.getId()));
        }

        Item updatedItem = ItemMapper.toItem(itemDTO, userId);
        updatedItem.setId(itemId);
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        return ItemMapper.toItemDTO(itemRepository.save(refreshItem(updatedItem)), comments);
    }

    @Override
    public ItemDTO findItemById(Long itemId, Long userId) {
        checkUser(userId);
        Item item = checkItem(itemId);
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        boolean trueOwner = item.getOwner().equals(userId);

        if (trueOwner) {
            LocalDateTime now = LocalDateTime.now();
            Sort sortDesc = Sort.by("start").descending();
            return constructItemDtoForOwner(item, comments, now, sortDesc);
        }

        return ItemMapper.toItemDTO(item, comments);
    }

    @Override
    public List<ItemDTO> findAllItemsByUserId(Long userId) {
        checkUser(userId);
        List<Item> userItems = itemRepository.findAll()
                .stream()
                .filter(item -> item.getOwner().equals(userId))
                .collect(Collectors.toList());
        List<ItemDTO> result = new ArrayList<>();
        staffItemDtoList(result, userItems, userId);
        result.sort((o1, o2) -> {
            if (o1.getNextBooking() == null && o2.getNextBooking() == null) {
                return 0;
            }
            if (o1.getNextBooking() != null && o2.getNextBooking() == null) {
                return -1;
            }
            if (o1.getNextBooking() == null) {
                return 1;
            }
            if (o1.getNextBooking().getStart().isBefore(o2.getNextBooking().getStart())) {
                return -1;
            }
            if (o1.getNextBooking().getStart().isAfter(o2.getNextBooking().getStart())) {
                return 1;
            }
            return 0;
        });
        return result;
    }

    @Override
    public List<ItemDTO> findItemsByRequest(String text, Long userId) {
        checkUser(userId);
        if (!StringUtils.hasText(text)) {
            return new ArrayList<>();
        }
        String updatedText = text.toLowerCase().trim();
        List<ItemDTO> result = new ArrayList<>();
        List<Item> found = itemRepository.search(updatedText);
        staffItemDtoList(result, found, userId);
        return result;
    }

    private Item refreshItem(Item patch) {
        Item item = checkItem(patch.getId());

        String name = patch.getName();
        if (StringUtils.hasText(name)) {
            item.setName(name);
        }

        String description = patch.getDescription();
        if (StringUtils.hasText(description)) {
            item.setDescription(description);
        }

        Boolean available = patch.getAvailable();
        if (available != null) {
            item.setAvailable(available);
        }
        return item;
    }

    private ItemDTO constructItemDtoForOwner(Item item, List<Comment> comments, LocalDateTime now, Sort sort) {
        Booking lastBooking = bookingRepository.findBookingByItemOwnerAndEndIsBefore(item.getId(), now, sort).stream().findFirst().orElse(null);
        Booking nextBooking = bookingRepository.findBookingByItemOwnerAndStartIsAfter(item.getId(), now, sort).stream().findFirst().orElse(null);
        return ItemMapper.toItemDTO(item, lastBooking, nextBooking, comments);
    }

    private void staffItemDtoList(List<ItemDTO> target, List<Item> found, Long userId) {
        LocalDateTime now = LocalDateTime.now();
        Sort sortDesc = Sort.by("start").descending();

        found.forEach(item -> {
            List<Comment> comments = commentRepository.findAllByItemId(item.getId());
            boolean trueOwner = item.getOwner().equals(userId);

            if (trueOwner) {
                ItemDTO itemDTO = constructItemDtoForOwner(item, comments, now, sortDesc);
                target.add(itemDTO);
            } else {
                target.add(ItemMapper.toItemDTO(item, comments));
            }
        });
    }

    @Override
    public CommentDTO addComment(AddCommentDTO addCommentDTO, Long itemId, Long userId) {
        if (!StringUtils.hasText(addCommentDTO.getText())) {
            log.warn("Комментарий не может быть пустым");
            throw new ValidateCommentException("Комментарий не может быть пустым");
        }
        Item item = checkItem(itemId);
        User author = checkUser(userId);

        if (bookingRepository.findBookingsForAddComments(itemId, userId, LocalDateTime.now()).isEmpty()) {
            log.warn("Нельзя оставить комментарий на предмет с Id: {}", itemId);
            throw new ValidateCommentException(String.format("Нельзя оставить комментарий на предмет с Id: %d", itemId));
        }
        Comment comment = CommentMapper.toComment(addCommentDTO, item, author);
        comment = commentRepository.save(comment);
        return CommentMapper.toCommentDTO(comment);
    }

    private Item checkItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> {
                    log.warn("Не найден предмет с id-{}: ", itemId);
                    return new NotFoundException(String.format(
                            "Не найден предмет с id: %d", itemId));
                }
        );
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
