package com.pico.server.dto;

import com.pico.server.dto.response.MemoryboxResponse;
import com.pico.server.entity.Anniversary;
import com.pico.server.entity.Letter;
import com.pico.server.entity.Memorybox;
import com.pico.server.entity.Photo;
import com.pico.server.entity.Schedule;
import com.pico.server.entity.Users;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record MemoryboxDto(
    Long memoryboxId,
    Boolean isOpen,
    String scheduleTitle,
    LocalDateTime scheduleStartTime,
    LocalDateTime scheduleEndTime,
    LocalDateTime opendate,
    List<Letter> letters,
    List<Photo> photos,

    MemoryUserDto author
) {
    public static MemoryboxDto of(Memorybox memorybox, Boolean isOpen, Schedule schedule) {
        return MemoryboxDto.builder()
            .memoryboxId(memorybox.getId())
            .isOpen(isOpen)
            .scheduleTitle(schedule.getTitle())
            .scheduleStartTime(memorybox.getScheduleStartTime())
            .scheduleEndTime(memorybox.getScheduleEndTime())
            .opendate(memorybox.getOpendate())
            .letters(memorybox.getLetters())
            .photos(memorybox.getPhotos())
            .build();
    }
}
