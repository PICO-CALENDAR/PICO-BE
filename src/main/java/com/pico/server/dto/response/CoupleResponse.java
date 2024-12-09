package com.pico.server.dto.response;

import java.util.List;

public record CoupleResponse<T>(
    List<T> couple
) {

    public static <T> CoupleResponse<T> from(List<T> couple) {
        return new CoupleResponse<>(couple);
    }
}
