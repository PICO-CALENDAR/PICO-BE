package com.pico.server.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    /**
     * Common Errors
     */
    UNKNOWN_SERVER_ERROR(500,"CM0001", "일시적으로 접속이 원활하지 않습니다. 서버 팀에 문의 부탁드립니다."),
    INVALID_INPUT_VALUE(400,"CM0002", "유효하지 않은 입력입니다."),
    INVALID_DATE(400,"CM0003", "유효하지 않은 연 또는 월입니다."),
    METHOD_NOT_ALLOWED(405,"CM0004", "허가되지 않은 메서드입니다."),
    IO_ERROR(500,"CM0005", "I/O 관련 에러입니다. 서버 팀에 문의 부탁드립니다."),
    INVALID_ROLE(400,"CM0006", "권한이 유효하지 않습니다."),
    INVALID_SEARCH_TYPE(400,"CM0007", "잘못된 검색 타입입니다."),
    INVALID_INPUT_DATE_VALUE(400,"CM0008", "잘못된 형태의 날짜 입력입니다."),
    INVALID_INPUT_YEAR_VALUE(400,"CM0009", "잘못된 형태의 연도 입력입니다."),

    /**
     * Auth Related Errors
     */
    NOT_MATCH_CATEGORY(400, "AU0001", "올바르지 않은 유형의 토큰입니다."),
    FAIL_REQUEST_TO_OAUTH2(400, "AU0002", "OAuth2 요청이 실패했습니다."),
    AUTHENTICATION_FAILED(401, "AU0003", "인증에 실패하였습니다."),
    TOKEN_AUTHENTICATION_FAILED(401, "AU0004", "토큰 인증에 실패하였습니다."),
    INVALID_TOKEN(401, "AU0005", "유효하지 않은 토큰입니다."),
    INVALID_PLATFORM(400, "AU0006", "유효하지 않은 플랫폼입니다."),
    AUTHORIZATION_FAILED(403, "AU0007", "접근 권한이 없습니다."),
    TOKEN_EXPIRED(403, "AU0008", "만료된 토큰입니다."),
    TOKEN_NOT_EXPIRED(403, "AU0009", "아직 토큰이 만료되지 않았습니다."),
    UNABLE_TO_SEND_EMAIL(400, "AU0010", "이메일 전송에 실패했습니다."),
    INVALID_AUTH_CODE(400, "AU0011", "유효하지 않은 인증 코드입니다."),
    NOT_MATCHED_AUTH_CODE(400, "AU0012", "올바르지 않은 인증 코드입니다."),
    TOKEN_VERIFY_FAILED(400, "AU0013", "토큰 검증에 실패 했습니다."),
    NOT_FOUND_APPLE_REFRESH_TOKEN(404, "AU0014", "해당 리프레시 토큰을 데이터베이스에서 찾을 수 없습니다."),


    /**
     * User Errors
     */
    NOT_FOUND_USER(404, "US0001", "해당 사용자를 찾을 수 없습니다."),
    ALREADY_EXIST_USER(400, "US0002", "이미 가입된 사용자입니다."),
    NOT_REGISTERED_USER(400, "US0003", "가입되지 않은 사용자입니다. 회원가입 후 이용해주세요"),
    NOT_FOUND_USER_DETAIL(404, "US0004", "해당 사용자 세부정보를 찾을 수 없습니다."),
    TERMS_NOT_AGREED(400, "US0005", "사용자 약관이 동의되지 않았습니다."),
    FAIL_TO_DELETE_APPLE_USER(400, "US0006", "사용자의 애플 정보를 삭제하는데 실패했습니다."),

    /**
     * Schedule Errors
     */
    NOT_FOUND_SCHEDULE(404, "SC0001", "해당 일정을 찾을 수 없습니다."),
    NOT_REPEAT_SCHEDULE(400, "SC0002", "반복 일정이 아닙니다."),
    NOT_ANNIVERSARY_SCHEDULE(400, "SC0003", "해당 일정은 기념일이 아닙니다."),
    NOT_COUPLE_ANNIVERSARY_SCHEDULE(400, "SC0004", "해당 일정은 커플 기념일이 아닙니다."),

    /**
     * Invite Code Errors
     */
    NOT_FOUND_INVITE_CODE(404, "IC0001", "해당 초대코드를 찾을 수 없습니다."),
    ALREADY_PARTNER_EXISTS(400, "IC0002", "이미 연인관계가 맺어진 사용자 입니다."),

    /**
     * Anniversary Errors
     */
    NOT_FOUND_ANNIVERSARY(404, "AN0001", "해당 기념일을 찾을 수 없습니다."),

    /**
     * Memorybox Errors
     */
    NOT_FOUND_MEMORYBOX(404, "MB0001", "해당 추억함을 찾을 수 없습니다."),
    DUPLICATE_MEMORYBOX(400, "MB0002", "같은 일정에 대해서는 추억함을 1개만 생성할 수 있습니다."),

    /**
     * Letter Errors
     */
    NOT_FOUND_LETTER(404, "LT0001", "해당 편지를 찾을 수 없습니다."),

    /**
     * Photo Errors
     */
    NOT_FOUND_PHOTO(404, "PH0001", "해당 사진을 찾을 수 없습니다.");

    private final int status;
    private final String code;
    private final String message;
}
