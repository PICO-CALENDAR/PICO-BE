package com.pico.server.controller;

import com.pico.server.dto.request.MemoryboxPartnerRequest;
import com.pico.server.dto.request.MemoryboxRequest;
import com.pico.server.dto.response.AnniversaryResponse;
import com.pico.server.dto.response.AuthResponse;
import com.pico.server.dto.response.ErrorResponse;
import com.pico.server.dto.response.ListResponse;
import com.pico.server.dto.response.MemoryboxResponse;
import com.pico.server.security.config.userid.LoginUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "추억함 API", description = "추억함 관련 API")
@RestController
@RequestMapping("/api/v1/memorybox")
@ApiResponse(responseCode = "200", description = "OK")
public interface MemoryboxApi {
    @GetMapping("/get/three/month/anniversaries")
    @Operation(summary = "앞으로 3개월 기념일 조회", description =  "앞으로 3개월 동안의 기념일을 조회합니다.")
    ResponseEntity<ListResponse<AnniversaryResponse>> getThreeMonthsAnniversaries();


    @PostMapping("/add/{anniversaryId}")
    @Operation(summary = "추억함 추가", description = "기념일에 해당하는 추억함을 생성합니다.")
    ResponseEntity<MemoryboxResponse> saveMemorybox(
        @Parameter(hidden = true)
        @LoginUserId Long userId,
        @PathVariable("anniversaryId") Long anniversaryId,
        @Valid
        @RequestBody MemoryboxRequest request
    ) throws IOException;

    @PostMapping("/add/partner/{memoryboxId}")
    @Operation(summary = "기존 추억함에 상대방 편지 및 사진 추가", description = "이미 연인이 생성한 추억함에 편지와 사진을 추가합니다.")
    ResponseEntity<MemoryboxResponse> addPartnerMemory(
        @PathVariable("memoryboxId") Long memoryboxId,
        @Valid
        @RequestBody MemoryboxPartnerRequest request
    ) throws IOException;

    @PatchMapping("/update/{memoryboxId}")
    @Operation(summary = "추억함 수정", description = "기념일에 해당하는 추억함을 수정합니다.")
    ResponseEntity<MemoryboxResponse> updateMemorybox(
        @Parameter(hidden = true)
        @LoginUserId Long userId,
        @PathVariable("memoryboxId") Long memoryboxId,
        @Valid
        @RequestBody MemoryboxUpdateRequest request
    );

    //update 한개 추가

    @DeleteMapping("/delete/{memoryboxId}")
    @Operation(summary = "추억함 삭제", description = "기념일에 해당하는 추억함을 삭제합니다.")
    ResponseEntity<MemoryboxResponse> deleteMemorybox(
        @Parameter(hidden = true)
        @LoginUserId Long userId,
        @PathVariable("memoryboxId") Long memoryboxId
    );

    //전체 delete 추가


    //조회 로직 전부 생성
}
