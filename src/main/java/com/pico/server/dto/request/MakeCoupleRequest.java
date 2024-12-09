package com.pico.server.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record MakeCoupleRequest(
    @Schema(description = "초대코드", example = "qwhgfudi213rsd8@")
    @NotNull(message = "inviteCode는 null 일 수 없습니다.")
    String inviteCode
) {

}
