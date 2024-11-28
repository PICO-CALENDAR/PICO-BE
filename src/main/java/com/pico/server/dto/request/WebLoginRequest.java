package com.pico.server.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record WebLoginRequest(
    @Schema(description = "인가 코드", example = "C95mSFwbWNVWUA1OCQ9mLSYFU5Dj7b5sFTgsy5lUNAAABjwtYyp")
    @NotNull(message = "code 는 null 일 수 없습니다.")
    String code,
    @Schema(description = "Redirect URI", example = "http://localhost:8080/login/oauth2/code/google")
    @NotNull(message = "redirectUri 은 null 일 수 없습니다.")
    String redirectUri
) {

}
