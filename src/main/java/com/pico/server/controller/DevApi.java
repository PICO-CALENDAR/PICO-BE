package com.pico.server.controller;

import com.pico.server.dto.request.WebLoginRequest;
import com.pico.server.dto.response.AuthResponse;
import com.pico.server.dto.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dev")
public interface DevApi {
    @Operation(summary = "소셜 로그인", description = "소셜 로그인을 진행합니다.")
    @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(
        mediaType = "application/json",
        examples = {
            @ExampleObject(name = "CM0002", description = "잘못된 플랫폼을 입력할 시 발생합니다.",
                value = """
                                    {"code": "CM0002", "message": "유효하지 않은 입력입니다."}
                                    """
            ),
            @ExampleObject(name = "AU0006", description = "지원하지 않는 플랫폼을 입력할 시 발생합니다.",
                value = """
                                    {"code": "AU0006", "message": "유효하지 않은 플랫폼입니다."}
                                    """
            ),
            @ExampleObject(name = "AU0002", description = "OAuth2 서버와 통신이 실패할 경우 발생합니다.",
                value = """
                                    {"code": "AU0002", "message": "OAuth2 요청이 실패했습니다."}
                                    """
            )
        }, schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/web/login/{provider}")
    ResponseEntity<AuthResponse> webLogin(
        @Valid
        @RequestBody WebLoginRequest request,
        @Parameter(example = "google", description = "oAuth 제공자 이름")
        @PathVariable("provider") String provider
    );
}
