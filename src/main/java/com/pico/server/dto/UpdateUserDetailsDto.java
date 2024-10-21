package com.pico.server.dto;

import lombok.Builder;

@Builder
public record UpdateUserDetailsDto(
    Long partnerId,
    String partnerName
) {

}
