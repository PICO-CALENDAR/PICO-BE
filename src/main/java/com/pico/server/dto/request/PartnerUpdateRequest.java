package com.pico.server.dto.request;

public record PartnerUpdateRequest(
    Long partnerId,
    String partnerName
) {

}
