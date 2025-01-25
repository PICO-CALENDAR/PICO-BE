package com.pico.server.dto.response;

import com.pico.server.entity.Anniversary;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record AnniversaryResponse(
    Long anniversaryId,
    String title,
    LocalDate date
) {
    public static AnniversaryResponse from(Anniversary anniversary) {
        return AnniversaryResponse.builder()
            .anniversaryId(anniversary.getId())
            .title(anniversary.getTitle())
            .date(anniversary.getDate())
            .build();
    }
}
