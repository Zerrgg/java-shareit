package ru.practicum.shareit.itemrequest;

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
import ru.practicum.shareit.itemrequest.dto.ItemRequestDTO;
import ru.practicum.shareit.itemrequest.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestControllerTest {

    public static final Long ID = 1L;
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final MockMvc mvc;

    private final ObjectMapper mapper;
    private ItemRequestDTO item;
    @MockBean
    private ItemRequestService itemRequestService;

    @BeforeEach
    public void init() throws Exception {

        item = ItemRequestDTO.builder()
                .id(1L)
                .description("description")
                .items(new ArrayList<>())
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void createRequestWithoutUser() throws Exception {

        mvc.perform(post("/requests")
                        .header(USER_ID_HEADER, -10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(item)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createRequestWithEmptyDescription() throws Exception {
        item.setDescription(null);

        mvc.perform(post("/requests")
                        .header(USER_ID_HEADER, ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(item)))
                .andExpect(status().is4xxClientError());
    }


    @Test
    void findAllByUserWithoutUser() throws Exception {

        mvc.perform(get("/requests"))
                .andExpect(status().is4xxClientError());
    }


    @Test
    void findAllByUserWithoutRequest() throws Exception {

        when(itemRequestService.findAllByUser(anyLong()))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/requests")
                        .header(USER_ID_HEADER, ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void findAllWithFrom0Size0() throws Exception {

        mvc.perform(get("/requests/all?from=0&size=0")
                        .header(USER_ID_HEADER, ID))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void findAllWithFromMin() throws Exception {

        mvc.perform(get("/requests/all?from=-1&size=20")
                        .header(USER_ID_HEADER, ID))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void findAllWithSizeMin() throws Exception {

        mvc.perform(get("/requests/all?from=0&size=-1")
                        .header(USER_ID_HEADER, ID))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createRequestTest() throws Exception {

        when(itemRequestService.createRequest(anyLong(), any()))
                .thenReturn(item);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(item)));

        verify(itemRequestService, times(1))
                .createRequest(anyLong(), any());
    }

    @Test
    void findAllByUserTest() throws Exception {
        when(itemRequestService.findAllByUser(anyLong()))
                .thenReturn(List.of(item));
        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(item))));

        verify(itemRequestService, times(1))
                .findAllByUser(anyLong());
    }

    @Test
    void findAllTest() throws Exception {
        when(itemRequestService.findAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(item));
        mvc.perform(get("/requests/all?from=0&size=10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(item))));

        verify(itemRequestService, times(1))
                .findAll(anyLong(), anyInt(), anyInt());
    }

    @Test
    void findByIdTest() throws Exception {
        when(itemRequestService.findById(anyLong(), anyLong()))
                .thenReturn(item);
        mvc.perform(get("/requests/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(item)));

        verify(itemRequestService, times(1)).findById(anyLong(), anyLong());
    }

}