package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.dto.BookingResponseDTO;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ValidateBookingException;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.user.dto.UserDTO;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {

    public static final Long ID = 2L;
    public static final String APPROVED_VALUE = "true";
    public static final String APPROVED_PARAM = "approved";
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @MockBean
    BookingService bookingService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    private BookingDTO inputDto;

    private BookingResponseDTO responseDto;

    @BeforeEach
    void init() {
        UserDTO userDto = UserDTO
                .builder()
                .id(ID)
                .name("username")
                .email("user@email.ru")
                .build();

        ItemDTO itemDto = ItemDTO
                .builder()
                .id(1L)
                .name("itemname")
                .description("descriptionitem")
                .available(true)
                .build();

        inputDto = BookingDTO
                .builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 10, 10, 10, 0))
                .end(LocalDateTime.of(2023, 10, 20, 10, 0))
                .bookerId(userDto.getId())
                .itemId(itemDto.getId())
                .status(WAITING)
                .build();

        responseDto = BookingResponseDTO
                .builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 10, 10, 10, 0))
                .end(LocalDateTime.of(2023, 10, 20, 10, 0))
                .status(WAITING)
                .item(itemDto)
                .booker(userDto)
                .build();
    }

    @Test
    void createBookingTest() throws Exception {
        when(bookingService.createBooking(any(), anyLong()))
                .thenReturn(responseDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(inputDto))
                        .header(USER_ID_HEADER, ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(responseDto)));

        verify(bookingService, times(1))
                .createBooking(any(), anyLong());
    }

    @Test
    void updateBookingTest() throws Exception {
        when(bookingService.updateBooking(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(responseDto);

        mvc.perform(patch("/bookings/1")
                        .param(APPROVED_PARAM, APPROVED_VALUE)
                        .header(USER_ID_HEADER, ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(responseDto)));

        verify(bookingService, times(1))
                .updateBooking(anyLong(), anyBoolean(), anyLong());
    }

    @Test
    void findBookingByIdTest() throws Exception {
        when(bookingService.findBookingById(anyLong(), anyLong()))
                .thenReturn(responseDto);

        mvc.perform(get("/bookings/1")
                        .header(USER_ID_HEADER, ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(responseDto)));

        verify(bookingService, times(1))
                .findBookingById(anyLong(), anyLong());
    }

    @Test
    void findBookingsByUserTest() throws Exception {
        when(bookingService.findBookingsByUser(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(responseDto));

        mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(responseDto))));

        verify(bookingService, times(1))
                .findBookingsByUser(anyString(), anyLong(), anyInt(), anyInt());
    }

    @Test
    void findBookingsByItemsOwnerTest() throws Exception {
        when(bookingService
                .findBookingsByItemsOwner(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(responseDto));

        mvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(responseDto))));

        verify(bookingService, times(1))
                .findBookingsByItemsOwner(anyString(), anyLong(), anyInt(), anyInt());
    }

    @Test
    void findBookingsByUserWrongStateTest() throws Exception {
        when(bookingService.findBookingsByUser(anyString(), anyLong(), anyInt(), anyInt()))
                .thenThrow(ValidateBookingException.class);
        mvc.perform(get("/bookings?state=text")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}