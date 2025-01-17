package com.pico.server.security.enums;

import java.security.InvalidParameterException;

public enum Platform {
    GOOGLE,
    APPLE;

    public static Platform fromString(String provider) {
        String platform = provider.toUpperCase();
        if (platform.equals("GOOGLE")) {
            return GOOGLE;
        }
        else if (platform.equals("APPLE")) {
            return APPLE;
        }
        throw new InvalidParameterException();
    }
}
