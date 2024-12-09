package com.pico.server.entity;

import com.pico.server.enums.RepeatType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RepeatInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "repeat_info_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private RepeatType repeatType;

    private LocalDateTime repeatStartDate;
    private LocalDateTime repeatEndDate;

    public void updateRepeatInfo(RepeatType repeatType, LocalDateTime repeatStartDate) {
        this.repeatType = repeatType;
        this.repeatStartDate = repeatStartDate;
    }

    public void updateRepeatEndDate(LocalDateTime repeatEndDate) {
        this.repeatEndDate = repeatEndDate;
    }
}
