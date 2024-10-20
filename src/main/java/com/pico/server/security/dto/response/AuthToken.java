package com.pico.server.security.dto.response;

public record AuthToken(
    String accessToken,
    String refreshToken,
    Boolean isRegistered
) {

    public static AuthToken of(String accessToken, String refreshToken, Boolean isRegistered) {
        return new AuthToken(accessToken, refreshToken, isRegistered);
    }
}
