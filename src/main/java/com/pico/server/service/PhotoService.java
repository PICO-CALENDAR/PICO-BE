package com.pico.server.service;

import com.pico.server.entity.Photo;
import com.pico.server.exception.ErrorCode;
import com.pico.server.exception.LetterException;
import com.pico.server.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PhotoService {
    private final PhotoRepository photoRepository;

    @Transactional(readOnly = true)
    public Photo findById(Long photoId) {
        return photoRepository.findById(photoId).orElseThrow(() -> new LetterException(ErrorCode.NOT_FOUND_PHOTO));
    }
}
