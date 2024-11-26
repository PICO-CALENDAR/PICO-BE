package com.pico.server.repository;

import com.pico.server.entity.RepeatDay;
import com.pico.server.entity.RepeatInfo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepeatDayRepository extends JpaRepository <RepeatDay, Long> {
    Optional<RepeatDay> deleteByRepeatInfo(RepeatInfo repeatInfo);
}
