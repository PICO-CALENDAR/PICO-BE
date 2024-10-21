package com.pico.server.dto.response;

import com.pico.server.security.dto.response.AuthToken;
import lombok.Builder;

@Builder
public record TokenResponse(
    String accessToken,
    String refreshToken,
    Boolean isRegistered
) {

    public static TokenResponse from(AuthToken authToken) {
        return TokenResponse.builder()
            .accessToken(authToken.accessToken())
            .refreshToken(authToken.refreshToken())
            .isRegistered(authToken.isRegistered())
            .build();
    }
}
