package com.pico.server.security.config;

import com.pico.server.security.properties.GoogleOAuth2Properties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(GoogleOAuth2Properties.class)
public class GoogleOAuth2Config {
}
