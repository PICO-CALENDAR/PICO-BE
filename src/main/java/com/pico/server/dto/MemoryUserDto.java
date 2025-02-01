package com.pico.server.dto;

import com.pico.server.entity.UserDetails;
import com.pico.server.entity.Users;
import lombok.Builder;

@Builder
public record MemoryUserDto(
    Long userId,
    String name,
    String nickName,
    String profileImage
) {
    public static MemoryUserDto from(Users user, UserDetails userDetails) {
        return MemoryUserDto.builder()
            .userId(user.getId())
            .name(userDetails.getName())
            .nickName(userDetails.getNickName())
            .profileImage(user.getProfileImage())
            .build();
    }
}
