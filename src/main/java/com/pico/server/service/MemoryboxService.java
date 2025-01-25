package com.pico.server.service;

import com.pico.server.dto.LetterDto;
import com.pico.server.dto.MemoryboxDto;
import com.pico.server.dto.PhotoDto;
import com.pico.server.dto.request.MemoryboxPartnerRequest;
import com.pico.server.dto.request.MemoryboxRequest;
import com.pico.server.entity.Anniversary;
import com.pico.server.entity.Letter;
import com.pico.server.entity.Memorybox;
import com.pico.server.entity.Photo;
import com.pico.server.entity.Schedule;
import com.pico.server.entity.Users;
import com.pico.server.exception.ErrorCode;
import com.pico.server.exception.MemoryboxException;
import com.pico.server.exception.ScheduleException;
import com.pico.server.repository.AnniversaryRepository;
import com.pico.server.repository.LetterRepository;
import com.pico.server.repository.MemoryboxRepository;
import com.pico.server.repository.PhotoRepository;
import com.pico.server.repository.ScheduleRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemoryboxService {

    private final MemoryboxRepository memoryboxRepository;
    private final PhotoRepository photoRepository;
    private final AnniversaryRepository anniversaryRepository;
    private final LetterRepository letterRepository;
    private final ScheduleRepository scheduleRepository;
    private final S3Service s3Service;

    private final String PHOTO_PREFIX = "PHOTO_";
    private final String USER_PREFIX = "USER_";

    @Transactional
    public MemoryboxDto saveMemoryBox(Users user, Anniversary anniversary, MemoryboxRequest request)
        throws IOException {
        Schedule schedule = scheduleRepository.findById(request.scheduleId()).orElseThrow(() -> new ScheduleException(
            ErrorCode.NOT_FOUND_SCHEDULE));
        LocalDateTime opendate = anniversary.getDate().atTime(LocalTime.of(9,0));
        String fileName = USER_PREFIX + user.getId().toString() + PHOTO_PREFIX + opendate.toString();
        String url = s3Service.putPhotoMultipartImage(request.photo(),fileName);

        Memorybox memorybox =  Memorybox.builder()
            .scheduleStartTime(request.scheduleStartTime())
            .scheduleEndTime(request.scheduleEndTime())
            .opendate(opendate)
            .user(user)
            .schedule(schedule)
            .anniversary(anniversary)
            .build();

        Letter letter = Letter.builder()
            .memorybox(memorybox)
            .content(request.letter())
            .build();

        Photo photo = Photo.builder()
            .memorybox(memorybox)
            .url(url)
            .build();

        memorybox.addLetters(letter);
        memorybox.addPhotos(photo);
        memoryboxRepository.save(memorybox);
        letterRepository.save(letter);
        photoRepository.save(photo);

        return MemoryboxDto.of(memorybox, schedule);
    }

    @Transactional
    public MemoryboxDto addPartnerMemory(Long memoryboxId, MemoryboxPartnerRequest request)
        throws IOException {
        Memorybox memorybox = memoryboxRepository.findById(memoryboxId)
            .orElseThrow(() -> new MemoryboxException(ErrorCode.NOT_FOUND_MEMORYBOX));
        Schedule schedule = memorybox.getSchedule();
        Hibernate.initialize(schedule);
        Users user = memorybox.getUser();
        Hibernate.initialize(user);

        String fileName = USER_PREFIX + memorybox.getUser().getId().toString() + PHOTO_PREFIX + memorybox.getOpendate().toString();
        String url = s3Service.putPhotoMultipartImage(request.photo(),fileName);

        Letter letter = Letter.builder()
            .memorybox(memorybox)
            .content(request.letter())
            .build();

        Photo photo = Photo.builder()
            .memorybox(memorybox)
            .url(url)
            .build();

        memorybox.addLetters(letter);
        memorybox.addPhotos(photo);
        memoryboxRepository.save(memorybox);
        letterRepository.save(letter);
        photoRepository.save(photo);

        return MemoryboxDto.of(memorybox, schedule);
    }


    @Transactional(readOnly = true)
    public List<Memorybox> findMemoryBoxes(Long anniversaryId, Long userId, Long partnerId) {

    }
    //anniversary, userId, partnerId 따라서 memorybox 조회
}
