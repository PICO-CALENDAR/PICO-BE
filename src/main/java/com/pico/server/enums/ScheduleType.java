package com.pico.server.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ScheduleType {
    MINE("나"),
    YOURS("상대"),
    OURS("우리");

    private final String value;
}
