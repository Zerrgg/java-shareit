package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {

    public static final Long ID = 1L;
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private ItemDTO itemDTO;

    private CommentDTO commentDTO;

    @BeforeEach
    void init() {
        itemDTO = ItemDTO
                .builder()
                .id(1L)
                .name("item name")
                .description("item description")
                .available(true)
                .build();

        commentDTO = CommentDTO
                .builder()
                .id(1L)
                .text("text of comment")
                .build();
    }

    @Test
    void createItemTest() throws Exception {

        when(itemService.createItem(any(ItemDTO.class), any(Long.class)))
                .thenReturn(itemDTO);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDTO))
                        .header(USER_ID_HEADER, ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDTO)));

        verify(itemService, times(1))
                .createItem(any(ItemDTO.class), any(Long.class));
    }

    @Test
    void addCommentTest() throws Exception {

        when(itemService.addComment(any(CommentDTO.class), any(Long.class), any(Long.class)))
                .thenReturn(commentDTO);


        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDTO))
                        .header(USER_ID_HEADER, ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(commentDTO)));
        verify(itemService, times(1))
                .addComment(any(CommentDTO.class), any(Long.class), any(Long.class));
    }

    @Test
    void updateItemTest() throws Exception {

        itemDTO.setName("updatedName");

        when(itemService.updateItem(any(ItemDTO.class), any(Long.class), any(Long.class)))
                .thenReturn(itemDTO);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID_HEADER, ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDTO)));

        verify(itemService, times(1))
                .updateItem(any(ItemDTO.class), any(Long.class), any(Long.class));
    }

    @Test
    void findItemByIdTest() throws Exception {

        when(itemService.findItemById(any(Long.class), any(Long.class)))
                .thenReturn(itemDTO);

        mvc.perform(get("/items/1")
                        .header(USER_ID_HEADER, ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDTO)));

        verify(itemService, times(1)).findItemById(any(Long.class), any(Long.class));
    }

    @Test
    void findAllItemsByUserIdTest() throws Exception {
        when(itemService.findAllItemsByUserId(any(Long.class), anyInt(), anyInt()))
                .thenReturn(List.of(itemDTO));

        mvc.perform(get("/items")
                        .param("from", "1")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDTO))));

        verify(itemService, times(1))
                .findAllItemsByUserId(any(Long.class), anyInt(), anyInt());
    }

    @Test
    void findItemsByRequestTest() throws Exception {
        when(itemService.findItemsByRequest(any(String.class), anyInt(), anyInt()))
                .thenReturn(List.of(itemDTO));

        mvc.perform(get("/items/search?text='name'")
                        .param("from", "1")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDTO))));

        verify(itemService, times(1))
                .findItemsByRequest(any(String.class), anyInt(), anyInt());
    }

}