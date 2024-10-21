package com.pico.server.dto.response;

import com.pico.server.entity.Users;
import com.pico.server.security.dto.response.AuthToken;
import lombok.Builder;

@Builder
public record AuthResponse(
    Long id,
    String name,
    String accessToken,
    String refreshToken,
    Boolean isRegistered
) {
    public static AuthResponse of(Users user, AuthToken authToken) {
        return AuthResponse.builder()
            .id(user.getId())
            .name(user.getName())
            .accessToken(authToken.accessToken())
            .refreshToken(authToken.refreshToken())
            .isRegistered(authToken.isRegistered())
            .build();
    }
}
