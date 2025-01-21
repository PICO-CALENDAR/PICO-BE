package com.pico.server.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pico.server.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record UserRegisterRequest(
    @Schema(description = "이름", example = "김피코")
    @NotNull(message = "name은 null 일 수 없습니다.")
    String name,

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
    LocalDate dday,

    @Schema(description = "서비스 약관 동의", example = "true")
    @NotNull(message = "isTermsAgreed는 null 일 수 없습니다.")
    Boolean isTermsAgreed,

    @Schema(description = "마케팅 약관 동의", example = "true")
    @Nullable
    Boolean isMarketingAgreed
) {

}
