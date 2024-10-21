package com.pico.server.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pico.server.enums.Gender;
import java.time.LocalDate;

public record UserInfoUpdateRequest(
    Gender gender,

    String nickName,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
    LocalDate birth
) {

}
