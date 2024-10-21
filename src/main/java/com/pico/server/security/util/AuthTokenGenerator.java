package com.pico.server.security.util;

import com.pico.server.dto.UserDto;
import com.pico.server.entity.Users;
import com.pico.server.security.dto.response.AuthToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthTokenGenerator {

    private final JwtAuthTokenUtil jwtAuthTokenUtil;

    public AuthToken generateAuthToken(Users user) {
        UserDto userDto = generateUserDto(user);

        Long userId = userDto.userId();
        String name = userDto.name();
        Boolean isRegistered = userDto.isRegistered();

        String newAccessToken = jwtAuthTokenUtil.createAccessToken(userId,name, isRegistered);
        String newRefreshToken = jwtAuthTokenUtil.createRefreshToken();

        return AuthToken.of(newAccessToken, newRefreshToken, isRegistered);
    }

    private UserDto generateUserDto(Users user) {
        return UserDto.builder()
            .userId(user.getId())
            .name(user.getName())
            .isRegistered(user.getIsRegistered())
            .build();
    }
}
