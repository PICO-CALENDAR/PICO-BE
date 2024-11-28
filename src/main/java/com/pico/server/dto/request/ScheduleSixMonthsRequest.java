package com.pico.server.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ScheduleSixMonthsRequest(
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy", timezone = "Asia/Seoul")
    @Schema(description = "일정 조회 요청 년도", example = "2024")
    @NotNull
    String year,

    @Schema(description = "상반기,하반기 여부 isStart true일 경우 상반기", example = "true")
    @NotNull
    Boolean isStart
) {

}
