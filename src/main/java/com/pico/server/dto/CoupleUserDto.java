package com.pico.server.dto;

import com.pico.server.entity.Users;
import lombok.Builder;

@Builder
public record CoupleUserDto(
    Long userId,
    String name
) {
    public static CoupleUserDto of(Users user) {
        return new CoupleUserDto(user.getId(), user.getUserDetails().getName());
    }
}
