package com.pico.server.dto;

import com.pico.server.security.enums.Platform;
import lombok.Builder;

@Builder
public record CreateUserDto(
    String name,
    String email,
    String profileImage,
    Platform platform,
    String platformId
) {

}
