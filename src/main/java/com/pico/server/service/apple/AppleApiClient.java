package com.pico.server.service.apple;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pico.server.dto.response.AppleTokenResponse;
import com.pico.server.dto.response.AppleUserInfoResponse;
import com.pico.server.exception.AuthException;
import com.pico.server.exception.ErrorCode;
import com.pico.server.security.enums.Platform;
import com.pico.server.security.validator.TokenValidator;
import io.jsonwebtoken.Claims;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.apache.http.auth.AuthenticationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class AppleApiClient {
    private final AppleKeyGenerator appleKeyGenerator;
    private final RestClient restClient;
    private final TokenValidator tokenValidator;
    @Value("${apple.url.issuer}")
    private String issuer;
    @Value("${apple.app.id}")
    private String clientId;

    public Platform oAuthProvider() {
        return Platform.APPLE;
    }

    /**
     * identityToken 으로부터 이메일을 얻어온다.
     * @param identityToken identityToken
     * @return 이메일, provider 정보
     */
    public AppleUserInfoResponse requestOauthUserInfo(String identityToken) {
        if (!isOauthTokenValid(identityToken)) {
            throw new AuthException(ErrorCode.INVALID_TOKEN);
        }
        String email = tokenValidator.parsePayLoad(identityToken).get("email");
        String name = tokenValidator.parsePayLoad(identityToken).get("name");
        return AppleUserInfoResponse.builder()
            .email(email)
            .name(name)
            .build();
    }

    /**
     * identity token 이 유효한 토큰인지 검사한다.
     * @param identityToken identityToken
     * @return true, false
     */
    public boolean isOauthTokenValid(String identityToken) {
        try {
            verifyIdentityToken(identityToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * IdentityToken을 검사한다.
     * @param identityToken
     * @throws JsonProcessingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    private void verifyIdentityToken(String identityToken) throws
        JsonProcessingException,
        NoSuchAlgorithmException,
        InvalidKeySpecException, AuthenticationException {
        Map<String, String> headers = tokenValidator.parseHeaders(identityToken);
        PublicKey publicKey = appleKeyGenerator.getPublicKey(headers);
        Claims tokenClaims = tokenValidator.validateTokenWithPublicKey(identityToken, publicKey);

        if (!issuer.equals(tokenClaims.getIssuer())) {
            throw new AuthException(ErrorCode.INVALID_TOKEN);
        }
        if (!clientId.equals(tokenClaims.getAudience())) {
            throw new AuthException(ErrorCode.INVALID_TOKEN);
        }
    }

    /**
     * apple 서버에서 발행하는 refresh token을 반환한다
     * @param authorizationCode
     * @return apple 에서 발행하는 refresh token
     * @throws IOException
     */
    public String getAppleRefreshToken(String authorizationCode) {
        MultiValueMap<String, String> body = getCreateTokenBody(authorizationCode);

        AppleTokenResponse appleTokenResponse = restClient.post()
            .uri("https://appleid.apple.com/auth/token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(body)
            .retrieve()
            .body(AppleTokenResponse.class);

        return Objects.requireNonNull(appleTokenResponse).refresh_token();
    }

    private MultiValueMap<String, String> getCreateTokenBody(String authorizationCode) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", authorizationCode);
        body.add("client_id", clientId);
        body.add("client_secret", appleKeyGenerator.getClientSecret());
        body.add("grant_type", "authorization_code");
        return body;
    }

    public void revokeToken(String refreshToken) {
        MultiValueMap<String, String> body = getRevokeTokenBody(refreshToken);

        restClient.post()
            .uri("https://appleid.apple.com/auth/revoke")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(body)
            .retrieve()
            .toBodilessEntity();
    }

    private MultiValueMap<String, String> getRevokeTokenBody(String refreshToken) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("token", refreshToken);
        body.add("client_secret", appleKeyGenerator.getClientSecret());
        body.add("token_type_hint", "refresh_token");
        return body;
    }
}
