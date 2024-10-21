package com.pico.server.security.dto.response.token;

public interface OAuth2TokenResponse {

    String getTokenType();

    String getAccessToken();

    String getScope();
}
