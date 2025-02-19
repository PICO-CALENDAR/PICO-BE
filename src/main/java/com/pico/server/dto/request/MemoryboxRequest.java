package com.pico.server.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

public record MemoryboxRequest(
    @Schema(description = "관련된 일정 원본 ID", example = "1")
    Long scheduleId,
    @Schema(description = "일정 시작 시간")
    @JsonFormat(timezone = "Asia/Seoul")
    LocalDateTime scheduleStartTime,
    @Schema(description = "일정 종료 시간")
    @JsonFormat(timezone = "Asia/Seoul")
    LocalDateTime scheduleEndTime,
    @Schema(description = "편지", example = "추억함에 등록할 편지 내용 입니다.")
    @NotNull
    String letter,
    @Schema(description = "사진", example = "추억함에 등록할 사진 입니다.")
    @NotNull
    MultipartFile photo

) {

}
