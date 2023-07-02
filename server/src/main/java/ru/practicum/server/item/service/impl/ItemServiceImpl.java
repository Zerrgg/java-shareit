package ru.practicum.server.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.practicum.server.booking.Booking;
import ru.practicum.server.booking.BookingMapper;
import ru.practicum.server.booking.BookingStatus;
import ru.practicum.server.booking.dto.BookingDTO;
import ru.practicum.server.booking.repository.BookingRepository;
import ru.practicum.server.comment.Comment;
import ru.practicum.server.comment.CommentMapper;
import ru.practicum.server.comment.dto.CommentDTO;
import ru.practicum.server.comment.repository.CommentRepository;
import ru.practicum.server.exception.DeniedAccessException;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.exception.ValidateCommentException;
import ru.practicum.server.item.Item;
import ru.practicum.server.item.ItemMapper;
import ru.practicum.server.item.dto.ItemDTO;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.item.service.ItemService;
import ru.practicum.server.user.User;
import ru.practicum.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

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
        Item item = ItemMapper.toItem(itemDTO, owner);
        item = itemRepository.save(item);
        return ItemMapper.toItemDTO(item);
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

        Item updatedItem = ItemMapper.toItem(itemDTO, owner);
        updatedItem.setId(itemId);
        List<CommentDTO> comments = CommentMapper.toDTOList(commentRepository.findAllByItemOrderByIdAsc(item));
        updatedItem = itemRepository.save(refreshItem(updatedItem));
        return ItemMapper.toItemWithCommentsDTO(updatedItem, comments);
    }

    @Override
    @Transactional
    public ItemDTO findItemById(Long itemId, Long userId) {
        checkUser(userId);
        Item item = checkItem(itemId);

        List<CommentDTO> comments = CommentMapper.toDTOList(commentRepository.findAllByItemOrderByIdAsc(item));

        List<Booking> bookings = bookingRepository.findAllByItemAndStatusOrderByStartAsc(item, BookingStatus.APPROVED);
        List<BookingDTO> bookingDTOList = bookings
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(toList());

        boolean trueOwner = item.getOwner().getId().equals(userId);

        if (trueOwner) {
            return ItemMapper.toItemWithBookingDTO(
                    item,
                    getLastBooking(bookingDTOList),
                    getNextBooking(bookingDTOList),
                    comments);
        }

        return ItemMapper.toItemWithCommentsDTO(item, comments);
    }

    @Override
    @Transactional
    public List<ItemDTO> findAllItemsByUserId(Long userId, int from, int size) {
        checkUser(userId);
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Item> userItems = itemRepository.findAllByOwnerIdOrderByIdAsc(userId, pageRequest);
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
                .map(item -> ItemMapper.toItemWithBookingDTO(
                        item,
                        getLastBooking(bookings.get(item.getId())),
                        getNextBooking(bookings.get(item.getId())),
                        comments.getOrDefault(item.getId(),
                                Collections.emptyList())))
                .collect(toList());
    }

    @Override
    public List<ItemDTO> findItemsByRequest(String text, int from, int size) {
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
        Comment comment = CommentMapper.toComment(commentDTO, item, author);
        comment.setCreated(LocalDateTime.now());
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
