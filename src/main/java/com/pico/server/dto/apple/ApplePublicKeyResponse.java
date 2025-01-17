package com.pico.server.dto.apple;

import java.util.List;
import org.apache.http.auth.AuthenticationException;

public record ApplePublicKeyResponse(List<ApplePublicKey> keys) {

    public ApplePublicKey getMatchedKey(String kid, String alg) throws AuthenticationException {
        return keys.stream()
            .filter(key -> key.kid().equals(kid) && key.alg().equals(alg))
            .findAny()
            .orElseThrow(AuthenticationException::new);
    }
}
