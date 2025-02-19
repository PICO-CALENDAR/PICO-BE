package com.pico.server.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record MemoryboxScheduleResponse(
    String anniversary,
    List<MemoryboxResponse> timeCapsules
) {
    public static MemoryboxScheduleResponse of(String anniversary, List<MemoryboxResponse> memoryboxes) {
        return MemoryboxScheduleResponse.builder()
            .anniversary(anniversary)
            .timeCapsules(memoryboxes)
            .build();
    }
}
