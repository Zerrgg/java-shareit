package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/bookings")
public class BookingController {

    public static final String DEFAULT_STATE_VALUE = "ALL";
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

}
