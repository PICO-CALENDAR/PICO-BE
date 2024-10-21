package com.pico.server.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pico.server.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record UserRegisterRequest(


    @Schema(description = "성별", example = "MALE")
    @NotNull(message = "gender는 null 일 수 없습니다.")
    Gender gender,
    @Schema(description = "닉네임", example = "닉네임모하지")
    @NotNull(message = "nickName은 null 일 수 없습니다.")
    String nickName,
    @Schema(description = "생년월일", example = "1995.10.23")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
    LocalDate birth
) {

}
