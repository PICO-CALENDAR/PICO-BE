package com.pico.server.controller;

import com.pico.server.dto.UserInfoDto;
import com.pico.server.dto.request.PartnerUpdateRequest;
import com.pico.server.dto.request.UserInfoUpdateRequest;
import com.pico.server.dto.request.UserRegisterRequest;
import com.pico.server.dto.response.AuthResponse;
import com.pico.server.dto.response.ErrorResponse;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "유저 API", description = "유저 관련 API")
@RestController
@SecurityRequirement(name = "JWT")
@RequestMapping("/api/v1/users")
@ApiResponse(responseCode = "200", description = "OK")
public interface UserApi {

    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "회원가입을 진행합니다.")
    @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(
        mediaType = "application/json",
        examples = {
            @ExampleObject(name = "US0001", description = "토큰에 담긴 UserId에 대한 사용자를 찾을 수 없을 때 발생합니다.",
                value = """
                                    {"code": "US0001", "message": "해당 사용자를 찾을 수 없습니다."}
                                    """
            )
        }, schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<AuthResponse> register(
        @Parameter(hidden = true)
        @LoginUserId Long userId,

        @Valid
        @RequestBody UserRegisterRequest request
    );

    @GetMapping("/info")
    @Operation(summary = "유저 정보 조회", description = "유저 정보를 조회합니다.")
    @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(
        mediaType = "application/json",
        examples = {
            @ExampleObject(name = "US0001", description = "사용자를 DB에서 찾을 수 없는 경우 발생합니다.",
                value = """
                                    {"code": "US0001", "message": "해당 사용자를 찾을 수 없습니다."}
                                    """
            )
        }, schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<UserInfoDto> getUserInfo(
        @Parameter(hidden = true)
        @LoginUserId Long userId
    );

    @Operation(summary = "유저 정보 수정", description = "유저 정보를 수정합니다.")
    @PatchMapping("/info")
    @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(
        mediaType = "application/json",
        examples = {
            @ExampleObject(name = "US0001", description = "사용자를 DB에서 찾을 수 없는 경우 발생합니다.",
                value = """
                                    {"code": "US0001", "message": "해당 사용자를 찾을 수 없습니다."}
                                    """
            )
        }, schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<UserInfoDto> updateUserInfo(
        @Parameter(hidden = true)
        @LoginUserId Long userId,

        @RequestBody UserInfoUpdateRequest request
    );

    @Operation(summary = "연인 정보 등록", description = "연인 정보를 등록합니다.")
    @PostMapping("/register/partner")
    @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(
        mediaType = "application/json",
        examples = {
            @ExampleObject(name = "US0001", description = "사용자를 DB에서 찾을 수 없는 경우 발생합니다.",
                value = """
                                    {"code": "US0001", "message": "해당 사용자를 찾을 수 없습니다."}
                                    """
            )
        }, schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<UserInfoDto> updatePartnerInfo(
        @Parameter(hidden = true)
        @LoginUserId Long userId,

        @RequestBody PartnerUpdateRequest request
    );
}
