package com.pico.server.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pico.server.enums.Gender;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record CreateUserDetailsDto(
    Gender gender,

    String nickName,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
    LocalDate birth
) {

}
