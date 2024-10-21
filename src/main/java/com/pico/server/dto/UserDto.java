package com.pico.server.dto;

import lombok.Builder;

@Builder
public record UserDto(
    Long userId,
    String name,
    Boolean isRegistered
) {

}
