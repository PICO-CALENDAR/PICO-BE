package com.pico.server.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record DeleteRepeatRequest(
    @JsonFormat(timezone = "Asia/Seoul")
    @Schema(description = "일정 삭제 요청 날짜")
    @NotNull
    LocalDateTime repeatEndDate
) {
}
