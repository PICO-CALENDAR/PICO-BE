package com.pico.server.repository;

import com.pico.server.entity.Users;
import com.pico.server.security.enums.Platform;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<Users,Long> {

    Optional<Users> findByPlatformAndPlatformId(Platform platform, String platformId);

    Optional<Users> findByPlatformAndEmail(Platform platform, String email);

    @Query("SELECT u FROM Users u JOIN FETCH u.userDetails WHERE u.userDetails.partnerId = :partnerId")
    Users findByPartnerId(@Param("partnerId") Long partnerId);

}
