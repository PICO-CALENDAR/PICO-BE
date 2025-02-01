package com.pico.server.repository;

import com.pico.server.entity.Memorybox;
import io.lettuce.core.dynamic.annotation.Param;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemoryboxRepository extends JpaRepository<Memorybox, Long> {

    boolean existsByScheduleScheduleIdAndScheduleUserIdAndScheduleStartTimeAndScheduleEndTime(Long scheduleId, Long userId,LocalDateTime startTime, LocalDateTime endTime);
    void deleteByUserId(Long userId);

    @Query("DELETE FROM Memorybox m where m.user.id = :userId OR m.user.id = :partnerId")
    void deleteByUserIdAndPartnerId(@Param("userId") Long userId, @Param("partnerId") Long partnerId);

    @Query("SELECT m FROM Memorybox m " +
        "WHERE m.user.id = :userId OR m.user.id = :partnerId")
    List<Memorybox> findByUserIdAndPartnerId(@Param("userId") Long userId,
    @Param("partnerId") Long partnerId);

    @Query("SELECT m FROM Memorybox m " +
        "WHERE m.schedule.scheduleId = :scheduleId " +
        "AND (m.user.id = :userId OR m.user.id = :partnerId)")
    List<Memorybox> findByUserIdAndPartnerIdAndScheduleId(@Param("userId") Long userId,
        @Param("partnerId") Long partnerId, @Param("scheduleId") Long scheduleId);

    List<Memorybox> findByUserIdAndOpendateIsBefore(Long userId, LocalDateTime opendate);

    List<Memorybox> findByUserIdAndOpendateIsAfter(Long userId, LocalDateTime opendate);
}
