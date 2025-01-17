package com.pico.server.dto.apple;

public record ApplePublicKey(
    String kty,
    String kid,
    String alg,
    String n,
    String e
) {

}
