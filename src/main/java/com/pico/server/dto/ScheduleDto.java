package com.pico.server.dto;

import com.pico.server.entity.Schedule;
import com.pico.server.enums.RepeatType;
import com.pico.server.enums.ScheduleType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale.Category;
import lombok.Builder;
import org.springframework.lang.Nullable;

@Builder
public record ScheduleDto(
    Long scheduleId,
    String title,
    Boolean isAllDay,
    LocalDateTime startTime,
    LocalDateTime endTime,
    ScheduleType category,

    @Nullable
    String meetingPeople,
    Boolean isRepeat,

    @Nullable
    RepeatType repeatType,

    @Nullable
    LocalDateTime repeatStartDate,

    @Nullable
    LocalDateTime repeatEndDate

) {
    public static ScheduleDto from(Schedule schedule) {
        return ScheduleDto.builder()
            .scheduleId(schedule.getScheduleId())
            .title(schedule.getTitle())
            .category(schedule.getCategory())
            .isAllDay(schedule.getIsAllDay())
            .startTime(schedule.getStartTime())
            .endTime(schedule.getEndTime())
            .meetingPeople(schedule.getMeetingPeople() != null ? schedule.getMeetingPeople() : null)
            .isRepeat(schedule.getIsRepeat())
            .repeatType(schedule.getRepeatInfo() != null ? schedule.getRepeatInfo().getRepeatType() : null)
            .repeatStartDate(schedule.getRepeatInfo() != null ? schedule.getRepeatInfo().getRepeatStartDate() : null)
            .repeatEndDate(schedule.getRepeatInfo() != null ? schedule.getRepeatInfo().getRepeatEndDate() : null)
            .build();
    }

    public static ScheduleDto fromPartner(Schedule schedule) {
        return ScheduleDto.builder()
            .scheduleId(schedule.getScheduleId())
            .title(schedule.getTitle())
            .category(ScheduleType.YOURS)
            .isAllDay(schedule.getIsAllDay())
            .startTime(schedule.getStartTime())
            .endTime(schedule.getEndTime())
            .meetingPeople(schedule.getMeetingPeople() != null ? schedule.getMeetingPeople() : null)
            .isRepeat(schedule.getIsRepeat())
            .repeatType(schedule.getRepeatInfo() != null ? schedule.getRepeatInfo().getRepeatType() : null)
            .repeatStartDate(schedule.getRepeatInfo() != null ? schedule.getRepeatInfo().getRepeatStartDate() : null)
            .repeatEndDate(schedule.getRepeatInfo() != null ? schedule.getRepeatInfo().getRepeatEndDate() : null)
            .build();
    }
}
