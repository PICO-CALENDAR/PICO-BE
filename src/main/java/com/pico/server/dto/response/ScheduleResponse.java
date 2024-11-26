package com.pico.server.dto.response;

import com.pico.server.dto.ScheduleDto;
import lombok.Builder;

@Builder
public record ScheduleResponse(
    Boolean success,
    ScheduleDto scheduleDto
) {
    public static ScheduleResponse of(Boolean success, ScheduleDto scheduleDto) {
        return new ScheduleResponse(success,scheduleDto);
    }
}
