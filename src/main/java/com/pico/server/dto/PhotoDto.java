package com.pico.server.dto;

import com.pico.server.entity.Photo;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;

@Builder
public record PhotoDto(
    Long photoId,
    String url
) {
    public static PhotoDto from(Photo photo) {
        return PhotoDto.builder()
            .photoId(photo.getId())
            .url(photo.getUrl())
            .build();
    }

    public static List<PhotoDto> from(List<Photo> photos) {
        List<PhotoDto> photoDtos = new ArrayList<>();
        for(Photo photo : photos) {
            photoDtos.add(PhotoDto.from(photo));
        }
        return photoDtos;
    }
}
