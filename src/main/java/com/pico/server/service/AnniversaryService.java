package com.pico.server.service;

import com.pico.server.entity.Anniversary;
import com.pico.server.repository.AnniversaryRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnniversaryService {
    private final AnniversaryRepository anniversaryRepository;

    @Transactional(readOnly = true)
    public List<Anniversary> findAllAnniversaries() {
        return anniversaryRepository.findAll();
    }

    @Transactional
    public Anniversary saveAnniversary(LocalDate date, String title) {
        return anniversaryRepository.save(Anniversary.builder()
            .date(date)
            .title(title)
            .build());
    }
}
