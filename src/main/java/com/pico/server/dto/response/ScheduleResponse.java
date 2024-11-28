package com.pico.server.dto.response;

import com.pico.server.dto.ScheduleDto;
import lombok.Builder;

@Builder
public record ScheduleResponse(
    Boolean success,
    ScheduleDto schedule
) {
    public static ScheduleResponse of(Boolean success, ScheduleDto schedule) {
        return new ScheduleResponse(success,schedule);
    }
}
