package com.pico.server.dto.request;

import com.pico.server.security.enums.Platform;
import lombok.Builder;
import org.springframework.lang.Nullable;

@Builder
public record CreateUserDto(
    String name,
    String email,

    @Nullable
    String profileImage,
    Platform platform,

    @Nullable
    String platformId
) {

}
