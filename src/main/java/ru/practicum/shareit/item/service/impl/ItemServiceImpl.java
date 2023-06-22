package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentMapper;
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
import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.comment.CommentMapper.*;
import static ru.practicum.shareit.item.ItemMapper.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDTO createItem(ItemDTO itemDTO, Long userId) {
        User owner = checkUser(userId);
        Item item = toItem(itemDTO, owner);
        item = itemRepository.save(item);
        return toItemDTO(item);
    }

    @Override
    @Transactional
    public ItemDTO updateItem(ItemDTO itemDTO, Long itemId, Long userId) {
        User owner = checkUser(userId);
        Item item = checkItem(itemId);
        boolean trueOwner = item.getOwner().getId().equals(userId);

        if (!trueOwner) {
            log.warn("Пользователь должен быть владельцем предмета");
            throw new DeniedAccessException(
                    String.format("Пользователь с Id: %d не является владельцем предмета c Id: %d ",
                            userId, item.getId()));
        }

        Item updatedItem = toItem(itemDTO, owner);
        updatedItem.setId(itemId);
        List<CommentDTO> comments = toDTOList(commentRepository.findAllByItemOrderByIdAsc(item));
        updatedItem = itemRepository.save(refreshItem(updatedItem));
        return toItemWithCommentsDTO(updatedItem, comments);
    }

    @Override
    @Transactional
    public ItemDTO findItemById(Long itemId, Long userId) {
        checkUser(userId);
        Item item = checkItem(itemId);

        List<CommentDTO> comments = toDTOList(commentRepository.findAllByItemOrderByIdAsc(item));

        List<Booking> bookings = bookingRepository.findAllByItemAndStatusOrderByStartAsc(item, BookingStatus.APPROVED);
        List<BookingDTO> bookingDTOList = bookings
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(toList());

        boolean trueOwner = item.getOwner().getId().equals(userId);

        if (trueOwner) {
            return toItemWithBookingDTO(
                    item,
                    getLastBooking(bookingDTOList),
                    getNextBooking(bookingDTOList),
                    comments);
        }

        return toItemWithCommentsDTO(item, comments);
    }

    @Override
    @Transactional
    public List<ItemDTO> findAllItemsByUserId(Long userId, int from, int size) {
        checkUser(userId);
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Item> userItems = itemRepository.findAllByOwnerId(userId, pageRequest);
        Map<Long, List<CommentDTO>> comments = commentRepository.findByItemIn(userItems)
                .stream()
                .map(CommentMapper::toCommentDTO)
                .collect(groupingBy(CommentDTO::getItemId, toList()));

        Map<Long, List<BookingDTO>> bookings = bookingRepository.findAllByItemInAndStatusOrderByStartAsc(userItems,
                        BookingStatus.APPROVED)
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(groupingBy(BookingDTO::getItemId, toList()));
        return userItems
                .stream()
                .map(item -> toItemWithBookingDTO(
                        item,
                        getLastBooking(bookings.get(item.getId())),
                        getNextBooking(bookings.get(item.getId())),
                        comments.getOrDefault(item.getId(),
                                Collections.emptyList())))
                .collect(toList());
    }

    @Override
    @Transactional
    public List<ItemDTO> findItemsByRequest(String text, int from, int size) {
        if (!StringUtils.hasText(text)) {
            return Collections.emptyList();
        }
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return itemRepository.search(text, pageRequest)
                .stream()
                .map(ItemMapper::toItemDTO)
                .collect(toList());
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

    @Override
    @Transactional
    public CommentDTO addComment(CommentDTO commentDTO, Long itemId, Long userId) {
        Item item = checkItem(itemId);
        User author = checkUser(userId);

        if (!StringUtils.hasText(commentDTO.getText())) {
            log.warn("Комментарий не может быть пустым");
            throw new ValidateCommentException("Комментарий не может быть пустым");
        }

        if (!bookingRepository.existsBookingByItemAndBookerAndStatusNotAndStartBefore(item, author, BookingStatus.REJECTED, LocalDateTime.now())) {
            log.warn("Нельзя оставить комментарий на предмет с Id: {}", itemId);
            throw new ValidateCommentException(String.format("Нельзя оставить комментарий на предмет с Id: %d", itemId));
        }
        Comment comment = toComment(commentDTO, item, author);
        comment.setCreated(LocalDateTime.now());
        comment = commentRepository.save(comment);
        return toCommentDTO(comment);
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

    private BookingDTO getLastBooking(List<BookingDTO> bookings) {
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }

        return bookings
                .stream()
                .filter(bookingDTO -> bookingDTO.getStart().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(BookingDTO::getStart))
                .orElse(null);
    }

    private BookingDTO getNextBooking(List<BookingDTO> bookings) {
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }

        return bookings
                .stream()
                .filter(bookingDTO -> bookingDTO.getStart().isAfter(LocalDateTime.now()))
                .findFirst()
                .orElse(null);
    }
}
