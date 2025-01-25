package com.pico.server.service;

import com.pico.server.dto.response.AnniversaryResponse;
import com.pico.server.entity.Anniversary;
import com.pico.server.exception.AnniversaryException;
import com.pico.server.exception.ErrorCode;
import com.pico.server.repository.AnniversaryRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Transactional(readOnly = true)
    public List<AnniversaryResponse> findThreeMonthsAnniversaries() {
        LocalDate now = LocalDate.now();
        LocalDate threeMonthsLater = now.plusMonths(3);
        List<AnniversaryResponse> anniversaryResponses = new ArrayList<>();

        List<Anniversary> anniversaries =  anniversaryRepository.findThreeMonthsAnniversary(now,threeMonthsLater);
        for(Anniversary anniversary : anniversaries) {
            anniversaryResponses.add(AnniversaryResponse.from(anniversary));
        }
        return anniversaryResponses;
    }

    @Transactional(readOnly = true)
    public Anniversary findById(Long anniversaryId) {
        return anniversaryRepository.findById(anniversaryId).orElseThrow(()-> new AnniversaryException(
            ErrorCode.NOT_FOUND_ANNIVERSARY));
    }

    @Transactional
    public Anniversary saveAnniversary(LocalDate date, String title) {
        return anniversaryRepository.save(Anniversary.builder()
            .date(date)
            .title(title)
            .build());
    }
}
