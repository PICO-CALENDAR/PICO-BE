package com.pico.server.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record AnniversaryMemoryboxRequest(
    @Schema(description = "기념일 이름", example = "발렌타인데이")
    @NotNull
    String title
) {

}
