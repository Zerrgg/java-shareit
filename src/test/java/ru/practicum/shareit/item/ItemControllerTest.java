package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemControllerTest {

    public static final Long ID = 1L;
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    public static final String FROM_VALUE = "0";
    public static final String SIZE_VALUE = "10";
    public static final String FROM_PARAM = "from";
    public static final String SIZE_PARAM = "size";

    @MockBean
    private ItemService itemService;

    private final ObjectMapper mapper;

    private final MockMvc mvc;

    private ItemDTO itemDTO;
    private CommentDTO commentDTO;

    @BeforeEach
    void init() {
        itemDTO = ItemDTO
                .builder()
                .id(1L)
                .name("itemName")
                .description("item Description")
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

        when(itemService.createItem(any(), anyLong()))
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
                .createItem(any(), anyLong());
    }

    @Test
    void addCommentTest() throws Exception {

        when(itemService.addComment(any(), anyLong(), anyLong()))
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
                .addComment(any(), anyLong(), anyLong());
    }


    @Test
    void addCommentWithEmptyText() throws Exception {

        when(itemService.addComment(any(), anyLong(), anyLong()))
                .thenReturn(commentDTO);

        commentDTO.setText("");

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDTO))
                        .header(USER_ID_HEADER, ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void updateItemTest() throws Exception {

        itemDTO.setName("updatedName");

        when(itemService.updateItem(any(), anyLong(), anyLong()))
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
                .updateItem(any(), anyLong(), anyLong());
    }

    @Test
    void findItemByIdTest() throws Exception {

        when(itemService.findItemById(anyLong(), anyLong()))
                .thenReturn(itemDTO);

        mvc.perform(get("/items/1")
                        .header(USER_ID_HEADER, ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDTO)));

        verify(itemService, times(1)).findItemById(anyLong(), anyLong());
    }

    @Test
    void findAllItemsByUserIdTest() throws Exception {
        when(itemService.findAllItemsByUserId(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDTO));

        mvc.perform(get("/items")
                        .param(FROM_PARAM, FROM_VALUE)
                        .param(SIZE_PARAM, SIZE_VALUE)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDTO))));

        verify(itemService, times(1))
                .findAllItemsByUserId(anyLong(), anyInt(), anyInt());
    }

    @Test
    void findItemsByRequestTest() throws Exception {
        when(itemService.findItemsByRequest(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDTO));

        mvc.perform(get("/items/search?text='name'")
                        .param(FROM_PARAM, FROM_VALUE)
                        .param(SIZE_PARAM, SIZE_VALUE)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDTO))));

        verify(itemService, times(1))
                .findItemsByRequest(anyString(), anyInt(), anyInt());
    }

    @Test
    void createItemWithoutSharerUserId() throws Exception {
        itemDTO = new ItemDTO(2L, "Дрель", "Простая дрель", true, null, null, null, null);

        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDTO)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createItemWithoutAvailable() throws Exception {
        itemDTO = new ItemDTO(2L, "Дрель", "Простая дрель", null, null, null, null, ID);

        mvc.perform(post("/items")
                        .header(USER_ID_HEADER, ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDTO)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createItemWithEmptyName() throws Exception {
        itemDTO = new ItemDTO(2L, "", "Простая дрель", true, null, null, null, ID);

        mvc.perform(post("/items")
                        .header(USER_ID_HEADER, ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDTO)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createItemWithEmptyDescription() throws Exception {
        itemDTO = new ItemDTO(2L, "Дрель", "", true, null, null, null, ID);

        mvc.perform(post("/items")
                        .header(USER_ID_HEADER, ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDTO)))
                .andExpect(status().is4xxClientError());
    }

}