package com.pico.server.security.validator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pico.server.exception.AuthException;
import com.pico.server.exception.ErrorCode;
import com.pico.server.security.util.JwtAuthTokenUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Map;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenValidator {

    private final JwtAuthTokenUtil jwtAuthTokenUtil;

    public void validateTokenSignature(String token) {
        if (Boolean.FALSE.equals(jwtAuthTokenUtil.isValidToken(token))) {
            throw new AuthException(ErrorCode.INVALID_TOKEN);
        }
    }

    public void validateExpiredToken(String token) {
        if (Boolean.TRUE.equals(jwtAuthTokenUtil.isExpired(token))) {
            throw new AuthException(ErrorCode.TOKEN_EXPIRED);
        }
    }

    public void validateTokenCategory(String category, String token) {
        String tokenCategory = jwtAuthTokenUtil.getCategory(token);
        if (Boolean.FALSE.equals(tokenCategory.equals(category))) {
            throw new AuthException(ErrorCode.NOT_MATCH_CATEGORY);
        }
    }

    public void checkNotExpiredToken(String token) {
        if (Boolean.FALSE.equals(jwtAuthTokenUtil.isExpired(token))) {
            throw new AuthException(ErrorCode.TOKEN_NOT_EXPIRED);
        }
    }

    public void validateRegistered(String accessToken) {
        if (Boolean.FALSE.equals(jwtAuthTokenUtil.isRegistered(accessToken))) {
            throw new AuthException(ErrorCode.NOT_REGISTERED_USER);
        }
    }

    public Map<String, String> parseHeaders(String token) throws JsonProcessingException {
        String header = token.split("\\.")[0];
        return new ObjectMapper().readValue(decodeBase64(header), Map.class);
    }

    public Map<String, String> parsePayLoad(String token) {
        String header = token.split("\\.")[1];
        try {
            return new ObjectMapper().readValue(decodeBase64(header), Map.class);
        } catch (JsonProcessingException e) {
            throw new AuthException(ErrorCode.INVALID_TOKEN);
        }
    }

    private String decodeBase64(String token) {
        return new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
    }

    public Claims validateTokenWithPublicKey(String token, PublicKey publicKey) {
        try {
            return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        } catch (MalformedJwtException e) {
            throw new AuthException(ErrorCode.INVALID_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new AuthException(ErrorCode.TOKEN_EXPIRED);
        }
    }
}
