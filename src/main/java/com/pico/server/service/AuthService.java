package com.pico.server.service;

import static com.pico.server.security.constants.JwtValues.JWT_PAYLOAD_VALUE_REFRESH;

import com.pico.server.dto.CreateUserDto;
import com.pico.server.entity.Users;
import com.pico.server.security.dto.response.AuthToken;
import com.pico.server.security.dto.response.token.OAuth2TokenResponse;
import com.pico.server.security.dto.response.userinfo.OAuth2UserInfoResponse;
import com.pico.server.security.enums.Platform;
import com.pico.server.security.util.AuthTokenGenerator;
import com.pico.server.security.util.JwtAuthTokenUtil;
import com.pico.server.security.util.OAuth2TokenManager;
import com.pico.server.security.util.OAuth2UserInfoManager;
import com.pico.server.security.validator.TokenValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final JwtAuthTokenUtil jwtAuthTokenUtil;
    private final TokenValidator tokenValidator;
    private final OAuth2TokenManager oauth2TokenManager;
    private final OAuth2UserInfoManager oAuth2UserInfoManager;
    private final UserService userService;
    private final AuthTokenGenerator authTokenGenerator;

    @Transactional
    public AuthToken login(Platform platform, String redirectUri, String code) {
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

    private CreateUserDto generateCreateUserDto(OAuth2UserInfoResponse userInfo) {
        return CreateUserDto.builder()
            .platform(Platform.fromString(userInfo.getProvider()))
            .platformId(userInfo.getProviderId())
            .email(userInfo.getEmail())
            .name(userInfo.getName())
            .profileImage(userInfo.getProfileImage())
            .build();
    }
}
