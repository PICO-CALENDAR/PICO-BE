package com.pico.server.controller;

import com.pico.server.dto.ScheduleDto;
import com.pico.server.dto.request.MemoryboxRequest;
import com.pico.server.dto.request.MemoryboxUpdateRequest;
import com.pico.server.dto.response.AnniversaryResponse;
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
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import org.springframework.http.MediaType;
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
@SecurityRequirement(name = "JWT")
@RequestMapping("/api/v1/memorybox")
@ApiResponse(responseCode = "200", description = "OK")
public interface MemoryboxApi {
    @GetMapping("/get/three/month/anniversaries")
    @Operation(summary = "앞으로 3개월 기념일 조회", description =  "앞으로 3개월 동안의 기념일을 조회합니다.")
    ResponseEntity<ListResponse<ScheduleDto>> getThreeMonthsAnniversaries(
        @Parameter(hidden = true)
        @LoginUserId Long userId
    );


    @PostMapping("/add")
    @Operation(summary = "추억함 추가", description = "기념일에 해당하는 추억함을 생성합니다.")
    @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(
        mediaType = "application/json",
        examples = {
            @ExampleObject(name = "US0001", description = "사용자를 찾을 수 없는 경우 발생합니다",
                value = """
                                    {"code": "US0001", "message": "해당 사용자를 찾을 수 없습니다."}
                                    """
            ),
            @ExampleObject(name = "AN0001", description = "기념일을 찾을 수 없는 경우 발생합니다",
                value = """
                                    {"code": "AN0001", "message": "해당 기념일을 찾을 수 없습니다."}
                                    """
            )
        }, schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(
        mediaType = "application/json",
        examples = {
            @ExampleObject(name = "MB0002", description = "같은 일정에 대해 추억함을 중복해 생성하려 할 때 발생합니다.",
                value = """
                                    {"code": "MB0002", "message": "같은 일정에 대해서는 추억함을 1개만 생성할 수 있습니다."}
                                    """
            )
        }, schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<MemoryboxResponse> saveMemorybox(
        @Parameter(hidden = true)
        @LoginUserId Long userId,
        @Valid
        @RequestBody MemoryboxRequest request
    ) throws IOException;

    @PatchMapping(value = "/update/{memoryboxId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "추억함 수정", description = "기념일에 해당하는 추억함의 편지와 사진을 수정합니다.")
    @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(
        mediaType = "application/json",
        examples = {
            @ExampleObject(name = "MB0001", description = "추억함을 찾을 수 없는 경우 발생합니다",
                value = """
                                    {"code": "MB0001", "message": "해당 추억함을 찾을 수 없습니다."}
                                    """
            ),
            @ExampleObject(name = "LT0001", description = "편지를 찾을 수 없는 경우 발생합니다",
                value = """
                                    {"code": "LT0001", "message": "해당 편지를 찾을 수 없습니다."}
                                    """
            ),
            @ExampleObject(name = "PH0001", description = "사진을 찾을 수 없는 경우 발생합니다",
                value = """
                                    {"code": "PH0001", "message": "해당 사진을 찾을 수 없습니다."}
                                    """
            )
        }, schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<MemoryboxResponse> updateMemorybox(
        @Parameter(hidden = true)
        @LoginUserId Long userId,
        @PathVariable("memoryboxId") Long memoryboxId,
        @Valid
        @RequestBody MemoryboxUpdateRequest request
    ) throws IOException;

    //update 한개 추가

    @DeleteMapping("/delete/{memoryboxId}")
    @Operation(summary = "추억함 삭제", description = "기념일에 해당하는 추억함을 삭제합니다.")
    @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(
        mediaType = "application/json",
        examples = {
            @ExampleObject(name = "MB0001", description = "추억함을 찾을 수 없는 경우 발생합니다",
                value = """
                                    {"code": "MB0001", "message": "해당 추억함을 찾을 수 없습니다."}
                                    """
            )
        }, schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<Boolean> deleteMemorybox(
        @Parameter(hidden = true)
        @LoginUserId Long userId,
        @PathVariable("memoryboxId") Long memoryboxId
    );

    @DeleteMapping("/delete/couple")
    @Operation(summary = "커플 유저에 해당하는 추억함 삭제", description = "내가 생성한 추억함과 상대방이 생성한 추억함 모두를 삭제합니다.")
    @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(
        mediaType = "application/json",
        examples = {
            @ExampleObject(name = "MB0001", description = "추억함을 찾을 수 없는 경우 발생합니다",
                value = """
                                    {"code": "MB0001", "message": "해당 추억함을 찾을 수 없습니다."}
                                    """
            )
        }, schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<Boolean> deleteCoupleMemorybox(
        @Parameter(hidden = true)
        @LoginUserId Long userId
    );

    @GetMapping("/get/all")
    @Operation(summary = "나와 상대방의 전체 추억함 조회", description = "나와 연인이 생성한 전체 추억함들을 조회합니다.")
    @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(
        mediaType = "application/json",
        examples = {
            @ExampleObject(name = "US0001", description = "사용자를 찾을 수 없는 경우 발생합니다",
                value = """
                                    {"code": "US0001", "message": "해당 사용자를 찾을 수 없습니다."}
                                    """
            )
        }, schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<ListResponse<MemoryboxResponse>> getAllMemoryboxes(
        @Parameter(hidden = true)
        @LoginUserId Long userId
    );

    @GetMapping("/get/anniversary/{scheduleId}")
    @Operation(summary = "기념일에 해당하는 추억함 조회", description = "나와 연인이 생성한 해당 기념일의 추억함들을 조회합니다.")
    @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(
        mediaType = "application/json",
        examples = {
            @ExampleObject(name = "US0001", description = "사용자를 찾을 수 없는 경우 발생합니다",
                value = """
                                    {"code": "US0001", "message": "해당 사용자를 찾을 수 없습니다."}
                                    """
            )
        }, schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<ListResponse<MemoryboxResponse>> getAnniversaryMemoryboxes(
        @Parameter(hidden = true)
        @LoginUserId Long userId,
        @PathVariable("scheduleId") Long scheduleId
    );

    @GetMapping("/get/opendate/past")
    @Operation(summary = "오픈 날짜가 지난 추억함 조회", description = "오픈 날짜가 지난 나의 추억함들을 조회합니다.")
    @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(
        mediaType = "application/json",
        examples = {
            @ExampleObject(name = "US0001", description = "사용자를 찾을 수 없는 경우 발생합니다",
                value = """
                                    {"code": "US0001", "message": "해당 사용자를 찾을 수 없습니다."}
                                    """
            )
        }, schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<ListResponse<MemoryboxResponse>> getMyPastMemoryboxes(
        @Parameter(hidden = true)
        @LoginUserId Long userId
    );

    @GetMapping("/get/opendate/upcoming")
    @Operation(summary = "오픈 날짜가 지나지않은 추억함 조회", description = "오픈 날짜가 지나지않은 나의 추억함들을 조회합니다.")
    @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(
        mediaType = "application/json",
        examples = {
            @ExampleObject(name = "US0001", description = "사용자를 찾을 수 없는 경우 발생합니다",
                value = """
                                    {"code": "US0001", "message": "해당 사용자를 찾을 수 없습니다."}
                                    """
            )
        }, schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<ListResponse<MemoryboxResponse>> getMyUpcomingMemoryboxes(
        @Parameter(hidden = true)
        @LoginUserId Long userId
    );

}
