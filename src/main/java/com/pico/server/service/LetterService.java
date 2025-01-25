package com.pico.server.service;

import com.pico.server.entity.Letter;
import com.pico.server.exception.ErrorCode;
import com.pico.server.exception.LetterException;
import com.pico.server.repository.LetterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LetterService {
    private final LetterRepository letterRepository;

    @Transactional(readOnly = true)
    public Letter findById(Long letterId) {
        return letterRepository.findById(letterId).orElseThrow(() -> new LetterException(ErrorCode.NOT_FOUND_LETTER));
    }
}
