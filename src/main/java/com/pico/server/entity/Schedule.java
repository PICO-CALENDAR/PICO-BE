package com.pico.server.entity;

import com.pico.server.enums.RepeatType;
import com.pico.server.enums.ScheduleType;
import com.pico.server.exception.ErrorCode;
import com.pico.server.exception.ScheduleException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
public class Schedule extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long scheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "repeat_info_id")
    private RepeatInfo repeatInfo;

    private String title;
    @Enumerated(EnumType.STRING)
    private ScheduleType category;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean isAllDay;
    private String meetingPeople;
    private Boolean isRepeat;

    public void updateRepeatInfo(RepeatInfo repeatInfo) {
        this.repeatInfo = repeatInfo;
    }
    public void deleteRepeatInfo() {
        this.repeatInfo = null;
    }
}
