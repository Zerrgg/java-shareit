package ru.practicum.server.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.server.comment.Comment;
import ru.practicum.server.item.Item;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByItemOrderByIdAsc(Item item);

    List<Comment> findByItemIn(List<Item> items);
}
