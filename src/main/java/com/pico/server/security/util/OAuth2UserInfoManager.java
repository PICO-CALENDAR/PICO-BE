package com.pico.server.security.util;

import com.pico.server.exception.AuthException;
import com.pico.server.exception.ErrorCode;
import com.pico.server.security.dto.response.token.OAuth2TokenResponse;
import com.pico.server.security.dto.response.userinfo.GoogleUserInfoResponse;
import com.pico.server.security.dto.response.userinfo.OAuth2UserInfoResponse;
import com.pico.server.security.enums.Platform;
import com.pico.server.security.properties.AppProperties;
import java.net.URI;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class OAuth2UserInfoManager extends OAuth2Manager {

    private final AppProperties appProperties;

    public OAuth2UserInfoResponse getUserInfo(Platform platform,
        OAuth2TokenResponse tokenResponse) {
        if (platform == Platform.GOOGLE) {
            return getGoogleUserInfoResponse(tokenResponse);
        }

        throw new AuthException(ErrorCode.INVALID_PLATFORM);
    }

    private OAuth2UserInfoResponse getGoogleUserInfoResponse(OAuth2TokenResponse tokenResponse) {
        RestClient restClient = createRestClient(appProperties.google().userInfoUri());
        String authorization = String.join(" ", tokenResponse.getTokenType(), tokenResponse.getAccessToken());

        Map<String, Object> response = requestUserInfoToGoogle(restClient, authorization);

        if (response != null) {
            return new GoogleUserInfoResponse(response);
        }
        throw new AuthException(ErrorCode.FAIL_REQUEST_TO_OAUTH2);
    }

    private Map<String, Object> requestUserInfoToGoogle(RestClient restClient, String authorization) {
        return restClient
            .get()
            .uri(getGoogleUserInfoUri())
            .header(HttpHeaders.AUTHORIZATION, authorization)
            .retrieve()
            .body(new ParameterizedTypeReference<>() {});
    }

    private URI getGoogleUserInfoUri() {
        String baseUrl = appProperties.google().userInfoUri();
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl);
        return uriBuilder.build().toUri();
    }
}
