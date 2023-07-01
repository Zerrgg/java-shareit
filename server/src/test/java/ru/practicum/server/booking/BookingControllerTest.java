package ru.practicum.server.booking;

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
import ru.practicum.server.booking.dto.BookingDTO;
import ru.practicum.server.booking.dto.BookingResponseDTO;
import ru.practicum.server.booking.service.BookingService;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.exception.ValidateBookingException;
import ru.practicum.server.item.dto.ItemDTO;
import ru.practicum.server.user.dto.UserDTO;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.server.booking.BookingStatus.WAITING;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerTest {

    public static final Long ID = 2L;
    public static final String APPROVED_VALUE = "true";
    public static final String APPROVED_PARAM = "approved";
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    public static final String FROM_VALUE = "0";
    public static final String SIZE_VALUE = "10";
    public static final String FROM_PARAM = "from";
    public static final String SIZE_PARAM = "size";

    @MockBean
    private BookingService bookingService;

    private final ObjectMapper mapper;

    private final MockMvc mvc;

    private BookingDTO inputDto;

    private BookingResponseDTO responseDto;

    @BeforeEach
    public void init() {
        UserDTO userDto = UserDTO
                .builder()
                .id(ID)
                .name("username")
                .email("user@email.ru")
                .build();

        ItemDTO itemDto = ItemDTO
                .builder()
                .id(1L)
                .name("itemName")
                .description("descriptionItem")
                .available(true)
                .build();

        inputDto = BookingDTO
                .builder()
                .id(1L)
                .start(LocalDateTime.now().plusMinutes(1).withNano(0))
                .end(LocalDateTime.now().plusDays(10).withNano(0))
                .bookerId(userDto.getId())
                .itemId(itemDto.getId())
                .status(WAITING)
                .build();

        responseDto = BookingResponseDTO
                .builder()
                .id(1L)
                .start(LocalDateTime.now().plusMinutes(1).withNano(0))
                .end(LocalDateTime.now().plusDays(10).withNano(0))
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
    void updateBookingWithoutBookingTest() throws Exception {
        when(bookingService.updateBooking(anyLong(), anyBoolean(), anyLong()))
                .thenThrow(new NotFoundException("Бронь не найдена"));

        mvc.perform(patch("/bookings/11")
                        .param(APPROVED_PARAM, APPROVED_VALUE)
                        .header(USER_ID_HEADER, ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Недопустимое значение {}")));

        verify(bookingService, times(1))
                .updateBooking(anyLong(), anyBoolean(), anyLong());
    }

    @Test
    void updateBookingWithUnknownUserTest() throws Exception {
        when(bookingService.updateBooking(anyLong(), anyBoolean(), anyLong()))
                .thenThrow(new NotFoundException("Бронь не найдена"));

        mvc.perform(patch("/bookings/1")
                        .param(APPROVED_PARAM, APPROVED_VALUE)
                        .header(USER_ID_HEADER, 11)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Недопустимое значение {}")));

        verify(bookingService, times(1))
                .updateBooking(anyLong(), anyBoolean(), anyLong());
    }

    @Test
    void updateBookingWithApproved() throws Exception {
        when(bookingService.updateBooking(anyLong(), anyBoolean(), anyLong()))
                .thenThrow(new ValidateBookingException("Статус APPROVED уже установлен"));

        mvc.perform(patch("/bookings/1")
                        .param(APPROVED_PARAM, APPROVED_VALUE)
                        .header(USER_ID_HEADER, ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Ошибка бронирования 400: ")));

        verify(bookingService, times(1))
                .updateBooking(anyLong(), anyBoolean(), anyLong());
    }

    @Test
    void updateBookingWithApprovedFalse() throws Exception {
        responseDto.setStatus(BookingStatus.REJECTED);

        when(bookingService.updateBooking(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(responseDto);

        mvc.perform(patch("/bookings/1")
                        .param(APPROVED_PARAM, "false")
                        .header(USER_ID_HEADER, ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));

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
                        .param(FROM_PARAM, FROM_VALUE)
                        .param(SIZE_PARAM, SIZE_VALUE)
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
        when(bookingService.findBookingsByItemsOwner(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(responseDto));

        mvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, ID)
                        .param(FROM_PARAM, FROM_VALUE)
                        .param(SIZE_PARAM, SIZE_VALUE)
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
                        .param(FROM_PARAM, FROM_VALUE)
                        .param(SIZE_PARAM, SIZE_VALUE)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}