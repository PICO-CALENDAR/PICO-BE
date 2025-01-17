package com.pico.server.repository;

import com.pico.server.entity.AppleRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppleRefreshTokenRepository extends JpaRepository<AppleRefreshToken, Long> {

}
