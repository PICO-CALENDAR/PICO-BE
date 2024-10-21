package com.pico.server.security.interceptor;

import static com.pico.server.security.constants.JwtValues.JWT_AUTHORIZATION_VALUE_PREFIX;
import static com.pico.server.security.constants.JwtValues.JWT_AUTHORIZATION_HEADER;

import com.pico.server.exception.AuthException;
import com.pico.server.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;

public class AuthorizationExtractor {

    public static String extractAccessToken(HttpServletRequest request) {
        String accessToken = request.getHeader(JWT_AUTHORIZATION_HEADER);

        if (accessToken == null) {
            throw new AuthException(ErrorCode.AUTHENTICATION_FAILED);
        }
        return accessToken.replace(JWT_AUTHORIZATION_VALUE_PREFIX, "");
    }
}
