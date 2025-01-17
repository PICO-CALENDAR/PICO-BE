package com.pico.server.dto.response;

import lombok.Builder;

@Builder
public record AppleTokenResponse(
    String access_token,
    String expires_in,
    String id_token,
    String refresh_token,
    String token_type,
    String error
) {

}
