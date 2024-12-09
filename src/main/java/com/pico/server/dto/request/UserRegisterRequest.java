package com.pico.server.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pico.server.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record UserRegisterRequest(


    @Schema(description = "성별", example = "MALE")
    @NotNull(message = "gender는 null 일 수 없습니다.")
    Gender gender,
    @Schema(description = "닉네임", example = "곽코딩")
    @NotNull(message = "nickName은 null 일 수 없습니다.")
    String nickName,
    @Schema(description = "생년월일", example = "1990-11-14",type = "string")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull(message = "birth는 null 일 수 없습니다.")
    LocalDate birth,

    @Schema(description = "디데이", example = "2023-10-23",type = "string")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull(message = "dday는 null 일 수 없습니다.")
    LocalDate dday
) {

}
