package com.pico.server.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

public record MemoryboxUpdateRequest(
    @Schema(name = "수정을 원하는 편지 ID", description = "수정할 편지 ID입니다.")
    @Nullable
    Long letterId,
    @Schema(name = "편지", description = "추억함에 등록할 편지 내용 입니다.")
    @Nullable
    String letter,

    @Schema(name = "수정을 원하는 사진 ID", description = "수정할 사진 ID입니다.")
    @Nullable
    Long photoId,
    @Schema(name = "사진", description = "추억함에 등록할 사진 입니다.")
    @Nullable
    MultipartFile photo
) {

}
