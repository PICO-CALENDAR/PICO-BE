package com.pico.server.controller.impl;

import com.pico.server.controller.DevApi;
import com.pico.server.dto.request.WebLoginRequest;
import com.pico.server.dto.response.AuthResponse;
import com.pico.server.entity.Users;
import com.pico.server.security.dto.response.AuthToken;
import com.pico.server.security.enums.Platform;
import com.pico.server.security.util.JwtAuthTokenUtil;
import com.pico.server.service.AuthService;
import com.pico.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class DevController implements DevApi {
    private final AuthService authService;
    private final UserService userService;
    private final JwtAuthTokenUtil jwtAuthTokenUtil;

    @Override
    public ResponseEntity<AuthResponse> webLogin(WebLoginRequest request, String provider) {
        Platform platform = Platform.fromString(provider);
        AuthToken authToken = authService.webLogin(platform, request.redirectUri(), request.code());

        Long userId = jwtAuthTokenUtil.getId(authToken.accessToken());
        Users loginUser = userService.findById(userId);

        AuthResponse response = AuthResponse.of(loginUser,authToken);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Boolean> revokeAppleTokenByAuthCode(String authorizationCode) {
        userService.revokeAppleTokenByAuthCode(authorizationCode);
        return ResponseEntity.ok(true);
    }
}
