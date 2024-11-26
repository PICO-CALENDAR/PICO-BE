package com.pico.server.dto.request;

import com.pico.server.enums.RepeatDayType;
import com.pico.server.enums.RepeatType;
import com.pico.server.enums.ScheduleType;
import java.time.LocalDateTime;
import java.util.List;

public record UpdateScheduleDto(
    String title,
    LocalDateTime startTime,
    LocalDateTime endTime,
    ScheduleType category,
    Boolean isAllDay,
    String meetingPeople,

    Boolean isRepeat,
    RepeatType repeat,
    List<RepeatDayType> repeatDays
) {

}
