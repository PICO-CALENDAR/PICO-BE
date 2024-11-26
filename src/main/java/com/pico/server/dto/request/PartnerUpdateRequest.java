package com.pico.server.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record PartnerUpdateRequest(
    @Schema(description = "상대방 유저 ID", example = "3")
    @NotNull(message = "partnerId는 null 일 수 없습니다.")
    Long partnerId,
    @Schema(description = "상대방 유저 닉네임", example = "민코딩")
    @NotNull(message = "partnerNickname는 null 일 수 없습니다.")
    String partnerNickname
) {

}
