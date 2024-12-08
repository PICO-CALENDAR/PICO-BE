package com.pico.server.dto.response;

import lombok.Builder;

@Builder
public record InviteCodeResponse(
    String inviteCode
) {
    public static InviteCodeResponse of(String inviteCode) {
        return new InviteCodeResponse(inviteCode);
    }
}
