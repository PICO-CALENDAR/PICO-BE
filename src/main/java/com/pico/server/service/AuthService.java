package com.pico.server.service;

import static com.pico.server.security.constants.JwtValues.JWT_PAYLOAD_VALUE_REFRESH;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.pico.server.dto.request.CreateUserDto;
import com.pico.server.dto.response.AppleUserInfoResponse;
import com.pico.server.entity.AppleRefreshToken;
import com.pico.server.entity.Users;
import com.pico.server.exception.AuthException;
import com.pico.server.exception.ErrorCode;
import com.pico.server.repository.AppleRefreshTokenRepository;
import com.pico.server.repository.UserRepository;
import com.pico.server.security.dto.response.AuthToken;
import com.pico.server.security.dto.response.token.OAuth2TokenResponse;
import com.pico.server.security.dto.response.userinfo.OAuth2UserInfoResponse;
import com.pico.server.security.enums.Platform;
import com.pico.server.security.properties.GoogleOAuth2Properties;
import com.pico.server.security.util.AuthTokenGenerator;
import com.pico.server.security.util.JwtAuthTokenUtil;
import com.pico.server.security.util.OAuth2TokenManager;
import com.pico.server.security.util.OAuth2UserInfoManager;
import com.pico.server.security.validator.TokenValidator;
import com.pico.server.service.apple.AppleApiClient;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final AppleApiClient appleApiClient;
    private final JwtAuthTokenUtil jwtAuthTokenUtil;
    private final TokenValidator tokenValidator;
    private final OAuth2TokenManager oauth2TokenManager;
    private final OAuth2UserInfoManager oAuth2UserInfoManager;
    private final UserService userService;
    private final AuthTokenGenerator authTokenGenerator;
    private final GoogleOAuth2Properties googleOAuth2Properties;
    private final AppleRefreshTokenRepository appleRefreshTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public AuthToken loginGoogle(String idToken) {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
            .setAudience(Arrays.asList(
                googleOAuth2Properties.androidClientId(),
                googleOAuth2Properties.iosClientId(),
                googleOAuth2Properties.androidWebClientId()))
            .build();

        GoogleIdToken googleIdToken;
        try {
            googleIdToken = verifier.verify(idToken);
            if (googleIdToken == null) {
                throw new AuthException(ErrorCode.INVALID_TOKEN);
            }
        } catch (Exception e) {
            throw new AuthException(ErrorCode.TOKEN_VERIFY_FAILED);
        }
        GoogleIdToken.Payload payload = googleIdToken.getPayload();
        Users newUser = userService.saveUser(generateCreateUserDtoWithGoogle(payload));

        Authentication authentication = new UsernamePasswordAuthenticationToken(
            newUser.getId(),
            null,
            Collections.emptyList()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authTokenGenerator.generateAuthToken(newUser);
    }

    @Transactional
    public AuthToken loginApple(String idToken, String authorizationCode) {
        AppleUserInfoResponse oauthUserInfo = appleApiClient.requestOauthUserInfo(idToken);

        if (oauthUserInfo.email() == null) {
            throw new AuthException(ErrorCode.FAIL_REQUEST_TO_OAUTH2);
        }

        Optional<Users> existUser = userRepository.findByPlatformAndEmail(Platform.APPLE, oauthUserInfo.email());
        Users newUser = userService.saveAppleUser(generateCreateUserDtoWithApple(oauthUserInfo.email()));

        if(existUser.isEmpty()) {
            registerAppleRefreshToken(newUser.getId(), authorizationCode);
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
            newUser.getId(),
            null,
            Collections.emptyList()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authTokenGenerator.generateAuthToken(newUser);
    }

    @Transactional
    public AuthToken webLogin(Platform platform, String redirectUri, String code) {
        OAuth2TokenResponse tokenResponse = oauth2TokenManager.getAccessToken(platform, redirectUri, code);
        OAuth2UserInfoResponse userInfo = oAuth2UserInfoManager.getUserInfo(platform, tokenResponse);
        Users newUser = userService.saveUser(generateCreateUserDto(userInfo));

        return authTokenGenerator.generateAuthToken(newUser);
    }

    public AuthToken reissue(String accessToken, String refreshToken) {
        tokenValidator.checkNotExpiredToken(accessToken);
        tokenValidator.validateExpiredToken(refreshToken);
        tokenValidator.validateTokenCategory(JWT_PAYLOAD_VALUE_REFRESH, refreshToken);
        tokenValidator.validateTokenSignature(refreshToken);
        Users findUser = userService.findById(jwtAuthTokenUtil.getId(refreshToken));

        return authTokenGenerator.generateAuthToken(findUser);
    }

    private void registerAppleRefreshToken(Long userId, String authorizationCode) {
        String refreshToken = appleApiClient.getAppleRefreshToken(authorizationCode);
        appleRefreshTokenRepository.save(
            AppleRefreshToken.builder()
                .userId(userId)
                .refreshToken(refreshToken)
                .build()
        );
    }

    private CreateUserDto generateCreateUserDto(OAuth2UserInfoResponse userInfo) {
        return CreateUserDto.builder()
            .platform(Platform.fromString(userInfo.getProvider()))
            .platformId(userInfo.getProviderId())
            .email(userInfo.getEmail())
            .name(userInfo.getName())
            .profileImage(userInfo.getProfileImage())
            .build();
    }

    private CreateUserDto generateCreateUserDtoWithGoogle(GoogleIdToken.Payload payload) {
        return CreateUserDto.builder()
            .platform(Platform.GOOGLE)
            .platformId(payload.getSubject())
            .email(payload.getEmail())
            .name((String) payload.get("name"))
            .profileImage((String) payload.get("picture"))
            .build();
    }

    private CreateUserDto generateCreateUserDtoWithApple(String email) {
        return CreateUserDto.builder()
            .platform(Platform.APPLE)
            .email(email)
            .build();
    }
}
