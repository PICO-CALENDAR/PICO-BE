package com.pico.server.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pico.server.enums.RepeatType;
import com.pico.server.enums.ScheduleType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public record UpdateScheduleDto(
    @Schema(description = "일정 이름", example = "공학관에서 회의")
    String title,
    @Schema(description = "일정 시작 시간")
    @JsonFormat(timezone = "Asia/Seoul")
    LocalDateTime startTime,
    @Schema(description = "일정 종료 시간")
    @JsonFormat(timezone = "Asia/Seoul")
    LocalDateTime endTime,
    @Schema(description = "일정 카테고리", example = "MINE")
    ScheduleType category,
    @Schema(description = "종일 일정 여부", example = "false")
    Boolean isAllDay,
    @Schema(description = "만나는 사람", example = "김피피,김코코,김피코")
    String meetingPeople,
    @Schema(description = "반복 일정 여부", example = "true")
    Boolean isRepeat,
    @Schema(description = "일정 반복 주기", example = "WEEKLY")
    RepeatType repeatType
) {

}
