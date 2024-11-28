package com.pico.server.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RepeatType {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY,
    BIWEEKLY;
}
