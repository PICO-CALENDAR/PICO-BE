package com.pico.server.security.properties;

import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = "google.oauth2")
public record GoogleOAuth2Properties(
    String androidClientId,
    String iosClientId
) {
    @ConstructorBinding
    public GoogleOAuth2Properties {
    }
}
