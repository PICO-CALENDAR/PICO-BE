package com.pico.server.repository;

import com.pico.server.entity.Users;
import com.pico.server.security.enums.Platform;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users,Long> {

    Optional<Users> findByPlatformAndPlatformId(Platform platform, String platformId);

}
