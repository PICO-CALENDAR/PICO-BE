package com.pico.server.security.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "app.token")
@ConfigurationPropertiesBinding
public record TokenProperties(
    String secretKey,
    @NestedConfigurationProperty TokenExpirationProperties expiration
) {
    @ConfigurationPropertiesBinding
    public record TokenExpirationProperties(
        Long accessTokenExpiration,
        Long refreshTokenExpiration
    ) {
        public TokenExpirationProperties {
            if (accessTokenExpiration == null) {
                throw new IllegalArgumentException("accessTokenExpiration이 null일 수 없습니다.");
            }
            if (refreshTokenExpiration == null){
                throw new IllegalArgumentException("refreshTokenExpiration이 null일 수 없습니다.");
            }
        }

    }
}
