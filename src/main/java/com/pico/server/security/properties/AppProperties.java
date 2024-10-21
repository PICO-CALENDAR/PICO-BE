package com.pico.server.security.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;

@ConfigurationProperties(prefix = "app")
@ConfigurationPropertiesBinding
public record AppProperties(
    GoogleProperties google
) {
    @ConfigurationPropertiesBinding
    public record GoogleProperties(
        String tokenUri,
        String userInfoUri,
        String clientId,
        String clientSecret
    ) { }
}
