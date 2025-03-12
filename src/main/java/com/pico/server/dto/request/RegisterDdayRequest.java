package com.pico.server.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record RegisterDdayRequest(
    @Schema(description = "디데이", example = "2023-10-23",type = "string")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull(message = "dday는 null 일 수 없습니다.")
    LocalDate dday
) {

}
