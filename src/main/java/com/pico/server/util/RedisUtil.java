package com.pico.server.util;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final ValueOperations<String, String> valueOperations;

    public Optional<String> getData(String key) {
        String value = valueOperations.get(key);

        if (value == null) {
            return Optional.empty();
        }
        return Optional.of(value);
    }
}
