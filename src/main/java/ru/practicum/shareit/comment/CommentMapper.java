package ru.practicum.shareit.comment;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.comment.dto.AddCommentDTO;
import ru.practicum.shareit.comment.dto.CommentDTO;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CommentMapper {
    public Comment toComment(AddCommentDTO addCommentDTO, Item item, User author) {
        Comment comment = new Comment();
        comment.setText(addCommentDTO.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

    public CommentDTO toCommentDTO(Comment comment) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setText(comment.getText());
        commentDTO.setAuthorName(comment.getAuthor().getName());
        commentDTO.setCreated(comment.getCreated());
        return commentDTO;
    }

    public static List<CommentDTO> toCommentDtoList(List<Comment> comments) {
        return comments
                .stream()
                .map(CommentMapper::toCommentDTO)
                .collect(Collectors.toList());
    }

}
