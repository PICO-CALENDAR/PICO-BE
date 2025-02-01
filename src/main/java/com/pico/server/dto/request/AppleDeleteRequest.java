package com.pico.server.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record AppleDeleteRequest(
    @Schema(description = "Apple RefreshToken 값", example = "dslafjkdsrtjlejldfkajlasljdf")
    @NotNull
    String refreshToken
) {

}
