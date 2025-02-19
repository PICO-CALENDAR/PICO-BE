package com.pico.server.repository;

import com.pico.server.dto.ScheduleDto;
import com.pico.server.entity.Schedule;
import com.pico.server.entity.Users;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    Optional<Schedule> findByUserIdAndScheduleId(Long userId, Long scheduleId);

    @Query("SELECT s FROM Schedule s WHERE (s.user.id = :userId OR s.user.id = :partnerId) AND s.isAnniversary = true")
    List<Schedule> findByUserIdAndPartnerIdAndIsAnniversaryTrue(@Param("userId") Long userId, @Param("partnerId") Long partnerId);

    @Query("SELECT s FROM Schedule s JOIN FETCH s.anniversary WHERE s.isAnniversary = true AND (s.user.id = :userId OR s.user.id = :partnerId) AND  s.anniversary.date >= :now AND s.anniversary.date <= :afterThreeMonths")
    List<Schedule> findThreeMonthsAnniversarys(@Param("now") LocalDate now, @Param("afterThreeMonths") LocalDate afterThreeMonths, @Param("userId") Long userId, @Param("partnerId") Long partnerId);

    List<Schedule> findByUserId(Long userId);

    void deleteAllByUser(Users user);

    @Query("SELECT s FROM Schedule s " +
        "WHERE s.user.id = :userId " +
        "AND (" +
        "  (FUNCTION('DATE_FORMAT', s.startTime, '%Y-%m-%d %H:%i') >= FUNCTION('DATE_FORMAT', :startDate, '%Y-%m-%d %H:%i') " +
        "   AND FUNCTION('DATE_FORMAT', s.startTime, '%Y-%m-%d %H:%i') <= FUNCTION('DATE_FORMAT', :endDate, '%Y-%m-%d %H:%i')) " +
        "  OR (FUNCTION('DATE_FORMAT', s.endTime, '%Y-%m-%d %H:%i') >= FUNCTION('DATE_FORMAT', :startDate, '%Y-%m-%d %H:%i') " +
        "   AND FUNCTION('DATE_FORMAT', s.endTime, '%Y-%m-%d %H:%i') <= FUNCTION('DATE_FORMAT', :endDate, '%Y-%m-%d %H:%i')) " +
        "  OR (FUNCTION('DATE_FORMAT', s.startTime, '%Y-%m-%d %H:%i') <= FUNCTION('DATE_FORMAT', :startDate, '%Y-%m-%d %H:%i') " +
        "   AND FUNCTION('DATE_FORMAT', s.endTime, '%Y-%m-%d %H:%i') >= FUNCTION('DATE_FORMAT', :endDate, '%Y-%m-%d %H:%i'))" +
        ")")
    List<Schedule> findSchedulesByUserIdAndDateRange(
        @Param("userId") Long userId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT s FROM Schedule s " +
        "WHERE s.user.id = :userId " +
        "AND s.isRepeat = false " +
        "AND (" +
        "  (FUNCTION('DATE_FORMAT', s.startTime, '%Y-%m-%d %H:%i') >= FUNCTION('DATE_FORMAT', :startDate, '%Y-%m-%d %H:%i') " +
        "   AND FUNCTION('DATE_FORMAT', s.startTime, '%Y-%m-%d %H:%i') <= FUNCTION('DATE_FORMAT', :endDate, '%Y-%m-%d %H:%i')) " +
        "  OR (FUNCTION('DATE_FORMAT', s.endTime, '%Y-%m-%d %H:%i') >= FUNCTION('DATE_FORMAT', :startDate, '%Y-%m-%d %H:%i') " +
        "   AND FUNCTION('DATE_FORMAT', s.endTime, '%Y-%m-%d %H:%i') <= FUNCTION('DATE_FORMAT', :endDate, '%Y-%m-%d %H:%i')) " +
        "  OR (FUNCTION('DATE_FORMAT', s.startTime, '%Y-%m-%d %H:%i') <= FUNCTION('DATE_FORMAT', :startDate, '%Y-%m-%d %H:%i') " +
        "   AND FUNCTION('DATE_FORMAT', s.endTime, '%Y-%m-%d %H:%i') >= FUNCTION('DATE_FORMAT', :endDate, '%Y-%m-%d %H:%i'))" +
        ")")
    List<Schedule> findSchedulesByUserIdAndDateRangeAndIsRepeatNot(
        @Param("userId") Long userId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT s FROM Schedule s " +
        "WHERE s.user.id = :userId " +
        "AND s.isRepeat = true")
    List<Schedule> findSchedulesByUserIdAndIsRepeat(
        @Param("userId") Long userId
    );

    @Query("""
    SELECT s FROM Schedule s
    WHERE s.user.id = :partnerId
    AND ((s.isCoupleAnniversary = true AND s.isAnniversary = true)
          OR (s.isCoupleAnniversary = false AND s.isAnniversary = false))
""")
    List<Schedule> findPartnerSchedules(@Param("partnerId") Long partnerId);

    @Query("DELETE FROM Schedule s WHERE (s.user.id = :userId OR s.user.userDetails.partnerId = :partnerId) AND s.isAnniversary = true")
    void deleteAnniversarySchedulesByUserIdOrPartnerId(@Param("userId") Long userId, @Param("partnerId") Long partnerId);

    @Query("SELECT s FROM Schedule s WHERE s.title = :title AND (s.user.id = :userId OR s.user.userDetails.partnerId = :partnerId)")
    List<Schedule> findByTitleAndUserIdAndPartnerId(@Param("title") String title, @Param("userId") Long userId, @Param("partnerId") Long partnerId);
}
