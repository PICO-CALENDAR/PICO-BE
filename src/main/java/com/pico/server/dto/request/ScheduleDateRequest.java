package com.pico.server.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ScheduleDateRequest(
    @Schema(description = "일정 조회 요청 날짜")
    @JsonFormat( timezone = "Asia/Seoul")
    @NotNull
    LocalDateTime todayDate
) {

}
