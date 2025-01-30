package com.pico.server.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

public record MemoryboxPartnerRequest(
    @Schema(description = "편지", example = "추억함에 등록할 편지 내용 입니다.")
    @NotNull
    String letter,
    @Schema(description = "사진", example = "추억함에 등록할 사진 입니다.")
    @Nullable
    MultipartFile photo
) {

}
