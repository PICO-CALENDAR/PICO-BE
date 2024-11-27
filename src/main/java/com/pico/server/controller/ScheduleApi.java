package com.pico.server.controller;

import com.pico.server.dto.request.CreateScheduleDto;
import com.pico.server.dto.request.UpdateScheduleDto;
import com.pico.server.dto.response.ScheduleResponse;
import com.pico.server.security.config.userid.LoginUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "일정 API", description = "일정 관련 API")
@RestController
@SecurityRequirement(name = "JWT")
@RequestMapping("/api/v1/schedule")
@ApiResponse(responseCode = "200", description = "OK")
public interface ScheduleApi {
    @PostMapping("/add")
    @Operation(summary = "일정 추가", description = "일정을 추가합니다.")
    ResponseEntity<ScheduleResponse> add(
        @Parameter(hidden = true)
        @LoginUserId Long userId,

        @Valid
        @RequestBody CreateScheduleDto createScheduleDto
    );

    @PostMapping("/delete/{scheduleId}")
    @Operation(summary = "일정 삭제", description = "일정을 삭제합니다.")
    ResponseEntity<ScheduleResponse> delete(
        @Parameter(hidden = true)
        @LoginUserId Long userId,
        @RequestParam Long scheduleId
    );

    @PatchMapping("/update/{scheduleId}")
    @Operation(summary = "일정 수정", description = "일정을 수정합니다.")
    ResponseEntity<ScheduleResponse> update(
        @Parameter(hidden = true)
        @LoginUserId Long userId,
        @RequestParam Long scheduleId,
        @ModelAttribute UpdateScheduleDto updateScheduleDto
    );
}
