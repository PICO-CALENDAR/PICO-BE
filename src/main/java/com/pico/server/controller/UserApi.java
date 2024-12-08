package com.pico.server.controller;

import com.pico.server.dto.UserInfoDto;
import com.pico.server.dto.request.MakeCoupleRequest;
import com.pico.server.dto.request.PartnerUpdateRequest;
import com.pico.server.dto.request.UpdateScheduleDto;
import com.pico.server.dto.request.UserInfoUpdateRequest;
import com.pico.server.dto.request.UserRegisterRequest;
import com.pico.server.dto.response.AuthResponse;
import com.pico.server.dto.response.CoupleResponse;
import com.pico.server.dto.response.ErrorResponse;
import com.pico.server.dto.response.ScheduleResponse;
import com.pico.server.entity.Users;
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
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "유저 API", description = "유저 관련 API")
@RestController
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

    @SecurityRequirement(name = "JWT")
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

    @SecurityRequirement(name = "JWT")
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
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "초대 코드 생성", description = "로그인된 유저의 초대코드를 생성합니다.")
    @GetMapping("/make/invite/code")
    @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(
        mediaType = "application/json",
        examples = {
            @ExampleObject(name = "US0001", description = "사용자를 DB에서 찾을 수 없는 경우 발생합니다.",
                value = """
                                    {"code": "US0001", "message": "해당 사용자를 찾을 수 없습니다."}
                                    """
            )
        }, schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(
        mediaType = "application/json",
        examples = {
            @ExampleObject(name = "IC0002", description = "이미 커플 관계가 맺어져있는 사용자일 경우 발생합니다.",
                value = """
                                    {"code": "IC0002", "message": "이미 연인관계가 맺어진 사용자 입니다."}
                                    """
            )
        }, schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<String> makeInviteCode(
        @Parameter(hidden = true)
        @LoginUserId Long userId
    );

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "커플 관계 맺기", description = "초대코드를 활용해 두 유저간의 커플 관계를 맺습니다.")
    @PostMapping("/make/couple")
    ResponseEntity<CoupleResponse<Users>> makeCouple(
        @Parameter(hidden = true)
        @LoginUserId Long userId,

        @Valid
        @RequestBody MakeCoupleRequest makeCoupleRequest
    );
}
