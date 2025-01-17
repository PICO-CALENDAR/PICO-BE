package com.pico.server.dto.response;

import lombok.Builder;

@Builder
public record AppleUserInfoResponse(
    String email,
    String name
) {

}
