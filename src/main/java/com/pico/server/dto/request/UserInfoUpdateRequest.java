package com.pico.server.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pico.server.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record UserInfoUpdateRequest(
    @Schema(description = "성별", example = "MALE")
    Gender gender,
    @Schema(description = "닉네임", example = "곽코딩")
    String nickName,
    @Schema(description = "생년월일", example = "1995-10-23")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate birth,

    @Schema(description = "디데이", example = "2023-10-21")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate dday,

    @Schema(description = "서비스 약관 동의", example = "true")
    Boolean isTermsAgreed,

    @Schema(description = "마케팅 약관 동의", example = "true")
    Boolean isMarketingAgreed
) {

}
