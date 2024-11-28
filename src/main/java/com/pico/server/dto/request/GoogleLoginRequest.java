package com.pico.server.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record GoogleLoginRequest(
    @Schema(description = "Google IdToken 값", example = "eyJhbGciOiJIUzI1NiJ9.eyJjYXRlZ29yeSI6ImFjY2VzcyIsImlkIjoxLCJuYW1lIjoi6rO966-87J6sIiwiaXNSZWdpc3RlcmVkIjpmYWxzZSwiaWF0IjoxNzMyNzk0NTk1LCJleHAiOjE3MzI3OTU3OTV9.LXFgXku5uDSZ6cNYlKYAYHbbcKWm0KbYYCWWNRwZbVE")
    @NotNull
    String idToken
) {

}
