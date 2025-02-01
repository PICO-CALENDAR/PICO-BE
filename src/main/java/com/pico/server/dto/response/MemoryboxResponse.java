package com.pico.server.dto.response;

import com.pico.server.dto.LetterDto;
import com.pico.server.dto.MemoryUserDto;
import com.pico.server.dto.MemoryboxDto;
import com.pico.server.dto.PhotoDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record MemoryboxResponse(
    Long memoryboxId,
    Boolean isOpen,
    String scheduleTitle,
    LocalDateTime scheduleStartTime,
    LocalDateTime scheduleEndTime,
    LocalDateTime opendate,
    List<LetterDto> letters,
    List<PhotoDto> photos,

    MemoryUserDto author
) {
    public static MemoryboxResponse of(MemoryboxDto memorybox, List<LetterDto> letters, List<PhotoDto> photos, MemoryUserDto author) {
        return MemoryboxResponse.builder()
            .memoryboxId(memorybox.memoryboxId())
            .isOpen(memorybox.isOpen())
            .scheduleTitle(memorybox.scheduleTitle())
            .scheduleStartTime(memorybox.scheduleStartTime())
            .scheduleEndTime(memorybox.scheduleEndTime())
            .opendate(memorybox.opendate())
            .letters(letters)
            .photos(photos)
            .author(author)
            .build();
    }
}
