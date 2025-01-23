package com.pico.server.service;

import com.pico.server.entity.Memorybox;
import com.pico.server.repository.AnniversaryRepository;
import com.pico.server.repository.MemoryboxRepository;
import com.pico.server.repository.PhotoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemoryboxService {

    private final MemoryboxRepository memoryboxRepository;
    private final PhotoRepository photoRepository;
    private final AnniversaryRepository anniversaryRepository;

    @Transactional
    public Memorybox saveMemoryBox() {
        return
    }
    //memorybox 저장 -> anniversary update


    @Transactional(readOnly = true)
    public List<Memorybox> findMemoryBoxes(Long anniversaryId, Long userId, Long partnerId) {
        List<Memorybox>
    }
    //anniversary, userId, partnerId 따라서 memorybox 조회
}
