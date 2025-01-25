package com.pico.server.dto;

import com.pico.server.entity.Letter;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;

@Builder
public record LetterDto(
    Long letterId,
    String content
) {
    public static LetterDto from(Letter letter) {
        return LetterDto.builder()
            .letterId(letter.getId())
            .content(letter.getContent())
            .build();
    }

    public static List<LetterDto> from(List<Letter> letters) {
        List<LetterDto> letterDtos = new ArrayList<>();
        for(Letter letter : letters) {
            letterDtos.add(LetterDto.from(letter));
        }
        return letterDtos;
    }
}
