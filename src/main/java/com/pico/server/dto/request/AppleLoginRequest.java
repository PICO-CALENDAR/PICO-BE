package com.pico.server.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record AppleLoginRequest(
    @Schema(description = "Apple IdToken 값", example = "dslafjkdsrtjlejldfkajlasljdf")
    @NotNull
    String idToken,
    @Schema(description = "Apple authorization code", example = "dslafjkdsrtjlejldfkajlasljdf")
    @NotNull
    String authorizationCode
) {

}
