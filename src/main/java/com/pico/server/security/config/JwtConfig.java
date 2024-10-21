package com.pico.server.security.config;

import com.pico.server.security.properties.TokenProperties;
import com.pico.server.security.util.JwtAuthTokenUtil;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    private final TokenProperties tokenProperties;
    private final SecretKey secretKey;

    public JwtConfig(TokenProperties tokenProperties) {
        this.tokenProperties = tokenProperties;

        this.secretKey = new SecretKeySpec(
            tokenProperties.secretKey().getBytes(StandardCharsets.UTF_8),
            Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    @Bean
    public JwtAuthTokenUtil jwtAuthTokenUtil() {
        return new JwtAuthTokenUtil(tokenProperties, secretKey);
    }

}
