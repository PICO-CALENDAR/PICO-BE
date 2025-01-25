package com.pico.server.dto.response;

import com.pico.server.dto.LetterDto;
import com.pico.server.dto.MemoryboxDto;
import com.pico.server.dto.PhotoDto;
import com.pico.server.entity.Anniversary;
import com.pico.server.entity.Memorybox;
import com.pico.server.entity.Users;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record MemoryboxResponse(
    Long memoryboxId,
    String scheduleTitle,
    LocalDateTime scheduleStartTime,
    LocalDateTime scheduleEndTime,
    LocalDateTime opendate,
    List<LetterDto> letters,
    List<PhotoDto> photos
) {
    public static MemoryboxResponse of(MemoryboxDto memorybox, List<LetterDto> letters, List<PhotoDto> photos) {
        return MemoryboxResponse.builder()
            .memoryboxId(memorybox.memoryboxId())
            .scheduleTitle(memorybox.scheduleTitle())
            .scheduleStartTime(memorybox.scheduleStartTime())
            .scheduleEndTime(memorybox.scheduleEndTime())
            .opendate(memorybox.opendate())
            .letters(letters)
            .photos(photos)
            .build();
    }
}
