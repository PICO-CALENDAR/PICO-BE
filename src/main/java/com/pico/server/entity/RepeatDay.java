package com.pico.server.entity;

import com.pico.server.enums.RepeatDayType;
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
public class RepeatDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "repeat_day_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private RepeatDayType repeatDayType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repeat_info_id")
    private RepeatInfo repeatInfo;
}
