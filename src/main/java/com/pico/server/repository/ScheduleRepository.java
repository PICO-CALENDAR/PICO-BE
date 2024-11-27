package com.pico.server.repository;

import com.pico.server.dto.ScheduleDto;
import com.pico.server.entity.Schedule;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    Optional<Schedule> findByUserIdAndScheduleId(Long userId, Long scheduleId);

    List<Schedule> findByUserId(Long userId);
}
