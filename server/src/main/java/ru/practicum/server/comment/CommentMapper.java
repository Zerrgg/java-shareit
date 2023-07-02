package ru.practicum.server.comment;

import lombok.experimental.UtilityClass;
import ru.practicum.server.comment.dto.CommentDTO;
import ru.practicum.server.item.Item;
import ru.practicum.server.user.User;

import java.util.List;

import static java.util.stream.Collectors.toList;

@UtilityClass
public class CommentMapper {
    public static Comment toComment(CommentDTO commentDTO, Item item, User author) {
        return Comment.builder()
                .id(commentDTO.getId())
                .text(commentDTO.getText())
                .item(item)
                .author(author)
                .created(commentDTO.getCreated())
                .build();
    }

    public static CommentDTO toCommentDTO(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .itemId(comment.getItem().getId())
                .build();
    }

    public static List<CommentDTO> toDTOList(List<Comment> comments) {
        return comments
                .stream()
                .map(CommentMapper::toCommentDTO)
                .collect(toList());
    }
}
