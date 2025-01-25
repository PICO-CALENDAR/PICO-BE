package com.pico.server.repository;

import com.pico.server.entity.Anniversary;
import io.lettuce.core.dynamic.annotation.Param;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AnniversaryRepository extends JpaRepository<Anniversary, Long> {
    @Query("SELECT a FROM Anniversary a WHERE a.date BETWEEN :startDate AND :endDate")
    List<Anniversary> findThreeMonthsAnniversary(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
