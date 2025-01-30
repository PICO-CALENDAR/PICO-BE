package com.pico.server.dto;

public record ErrorReportDto(
    String errorMessage,
    String payload
) {
}

