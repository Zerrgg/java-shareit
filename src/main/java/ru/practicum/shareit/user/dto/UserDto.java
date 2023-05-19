package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.exception.markers.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotNull(groups = {Create.class})
    private String name;
    @Email(groups = {Create.class})
    @NotNull(groups = {Create.class})
    private String email;
}
