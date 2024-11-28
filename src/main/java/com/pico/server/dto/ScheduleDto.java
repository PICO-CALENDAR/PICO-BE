package com.pico.server.dto;

import com.pico.server.entity.Schedule;
import com.pico.server.enums.RepeatType;
import com.pico.server.enums.ScheduleType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record ScheduleDto(
    Long scheduleId,
    String title,
    Boolean isAllDay,
    LocalDateTime startTime,
    LocalDateTime endTime,
    ScheduleType category,
    String meetingPeople,
    Boolean isRepeat,
    RepeatType repeatType,
    LocalDateTime repeatStartDate,
    LocalDateTime repeatEndDate

) {
    public static ScheduleDto from(Schedule schedule) {
        return ScheduleDto.builder()
            .scheduleId(schedule.getScheduleId())
            .title(schedule.getTitle())
            .isAllDay(schedule.getIsAllDay())
            .startTime(schedule.getStartTime())
            .endTime(schedule.getEndTime())
            .meetingPeople(schedule.getMeetingPeople())
            .isRepeat(schedule.getIsRepeat())
            .repeatType(schedule.getRepeatInfo().getRepeatType())
            .repeatStartDate(schedule.getRepeatInfo().getRepeatStartDate())
            .repeatEndDate(schedule.getRepeatInfo().getRepeatEndDate())
            .build();
    }
}
