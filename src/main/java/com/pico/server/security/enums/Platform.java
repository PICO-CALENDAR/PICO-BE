package com.pico.server.security.enums;

import java.security.InvalidParameterException;

public enum Platform {
    GOOGLE;

    public static Platform fromString(String provider) {
        String platform = provider.toUpperCase();
        if (platform.equals("GOOGLE")) {
            return GOOGLE;
        }
        throw new InvalidParameterException();
    }
}
