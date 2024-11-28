package com.pico.server.repository;

import com.pico.server.dto.ScheduleDto;
import com.pico.server.entity.Schedule;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    Optional<Schedule> findByUserIdAndScheduleId(Long userId, Long scheduleId);

    List<Schedule> findByUserId(Long userId);

    @Query("SELECT s FROM Schedule s WHERE s.user.id = :userId " +
        "AND FUNCTION('DATE', s.startTime) <= FUNCTION('DATE', :todayDate) " +
        "AND FUNCTION('DATE', s.endTime) >= FUNCTION('DATE', :todayDate)")
    List<Schedule> findSchedulesByUserIdAndTodayDate(@Param("userId") Long userId, @Param("todayDate") LocalDateTime todayDate);

    @Query("SELECT s FROM Schedule s " +
        "WHERE s.user.id = :userId " +
        "AND FUNCTION('DATE_FORMAT', s.startTime, '%Y-%m-%d %H:%i') >= FUNCTION('DATE_FORMAT', :startDate, '%Y-%m-%d %H:%i') " +
        "AND FUNCTION('DATE_FORMAT', s.endTime, '%Y-%m-%d %H:%i') <= FUNCTION('DATE_FORMAT', :endDate, '%Y-%m-%d %H:%i')")
    List<Schedule> findSchedulesByUserIdAndDateRange(
        @Param("userId") Long userId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}
