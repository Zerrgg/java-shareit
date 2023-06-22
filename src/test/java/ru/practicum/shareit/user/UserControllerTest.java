package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {

    public static final Long ID = 1L;
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";


    private final ObjectMapper mapper;

    @MockBean
    private UserService userService;

    private final MockMvc mvc;

    private UserDTO userDto;

    @BeforeEach
    public void init() {
        userDto = UserDTO
                .builder()
                .id(ID)
                .name("user name")
                .email("user@email.com")
                .build();
    }

    @Test
    void createUserTest() throws Exception {

        when(userService.createUser(any(UserDTO.class)))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userDto)));

        verify(userService, times(1))
                .createUser(any(UserDTO.class));
    }

    @Test
    void createUserWhenFailName() throws Exception {
        userDto = new UserDTO(2L, "", "user@user.com");

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createUserWhenFailEmail() throws Exception {
        userDto = new UserDTO(2L, "user", "");

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Ошибка валидации")));
    }

    @Test
    void findUserByIdTest() throws Exception {

        when(userService.findUserById(anyLong()))
                .thenReturn(userDto);

        mvc.perform(get("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userDto)));

        verify(userService, times(1))
                .findUserById(anyLong());
    }

    @Test
    void findAllUsersTest() throws Exception {

        when(userService.findAllUsers())
                .thenReturn(List.of(userDto));

        mvc.perform(get("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(userDto))));

        verify(userService, times(1))
                .findAllUsers();
    }


    @Test
    void updateUserTest() throws Exception {
        userDto.setName("updatedName");

        when(userService.updateUser(anyLong(), any()))
                .thenReturn(userDto);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userDto)));

        verify(userService, times(1))
                .updateUser(anyLong(), any());
    }

    @Test
    void updateUserWhenFailEmail() throws Exception {
        userDto = new UserDTO(2L, "user", "");

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Ошибка валидации")));
    }

    @Test
    void updateUserPatchUserName() throws Exception {
        String jsonUser = "{\"name\":\"updateName\"}";

        when(userService.updateUser(anyLong(), any()))
                .thenReturn(userDto);

        mvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUser))
                .andExpect(status().isOk());

        verify(userService, times(1))
                .updateUser(anyLong(), any());
    }

    @Test
    void updateUserPatchUserEmail() throws Exception {
        String jsonUser = "{\"email\":\"updateName@user.com\"}";

        when(userService.updateUser(anyLong(), any()))
                .thenReturn(userDto);

        mvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUser))
                .andExpect(status().isOk());

        verify(userService, times(1))
                .updateUser(anyLong(), any());
    }

    @Test
    void deleteUserByIdTest() throws Exception {

        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService, times(1))
                .deleteUserById(anyLong());
    }

}