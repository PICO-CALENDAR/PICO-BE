package com.pico.server.controller;

import com.pico.server.dto.ScheduleDto;
import com.pico.server.dto.request.CreateScheduleDto;
import com.pico.server.dto.request.DeleteRepeatRequest;
import com.pico.server.dto.request.ScheduleDateRequest;
import com.pico.server.dto.request.ScheduleSixMonthsRequest;
import com.pico.server.dto.request.UpdateScheduleDto;
import com.pico.server.dto.response.ListResponse;
import com.pico.server.dto.response.ScheduleResponse;
import com.pico.server.security.config.userid.LoginUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        @PathVariable("scheduleId") Long scheduleId
    );

    @PostMapping("/delete/repeat/{scheduleId}")
    @Operation(summary = "반복 일정 삭제", description = "오늘 일정, 오늘 이후 일정에 대해 반복 일정을 삭제합니다.")
    ResponseEntity<ScheduleResponse> deleteRepeatSchedule(
        @Parameter(hidden = true)
        @LoginUserId Long userId,
        @PathVariable("scheduleId") Long scheduleId,
        @RequestBody DeleteRepeatRequest deleteRepeatRequest
    );

    @PatchMapping("/update/{scheduleId}")
    @Operation(summary = "일정 수정", description = "일정을 수정합니다.")
    ResponseEntity<ScheduleResponse> update(
        @Parameter(hidden = true)
        @LoginUserId Long userId,
        @PathVariable("scheduleId") Long scheduleId,
        @RequestBody UpdateScheduleDto updateScheduleDto
    );

    @GetMapping("/get/detail/{scheduleId}")
    @Operation(summary = "단일 세부 일정 조회", description = "한 일정의 세부 일정을 조회합니다.")
    ResponseEntity<ScheduleResponse> getOneSchedule(
        @Parameter(hidden = true)
        @LoginUserId Long userId,
        @PathVariable("scheduleId") Long scheduleId
    );

    @GetMapping("/get/today/schedules")
    @Operation(summary = "당일 전체 일정 조회", description = "요청 받은 날짜와 사용자에 해당 하는 일정을 전부 조회합니다.")
    ResponseEntity<ListResponse<ScheduleDto>> getTodaySchedules(
        @Parameter(hidden = true)
        @LoginUserId Long userId,
        @Valid
        @ModelAttribute ScheduleDateRequest scheduleDateRequest
    );

    @GetMapping("/get/six/months/schedules")
    @Operation(summary = "6개월치 전체 일정 조회", description = "요청 받은 년도,상반기 하반기 여부에 따라 사용자에 해당 하는 일정을 전부 조회합니다.")
    ResponseEntity<ListResponse<ScheduleDto>> getSixMonthsSchedules(
        @Parameter(hidden = true)
        @LoginUserId Long userId,
        @Valid
        @ModelAttribute ScheduleSixMonthsRequest scheduleSixMonthsRequest
    );
}
