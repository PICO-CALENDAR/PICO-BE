package com.pico.server.service;

import com.pico.server.dto.LetterDto;
import com.pico.server.dto.MemoryboxDto;
import com.pico.server.dto.PhotoDto;
import com.pico.server.dto.request.MemoryboxRequest;
import com.pico.server.dto.request.MemoryboxUpdateRequest;
import com.pico.server.dto.response.MemoryboxResponse;
import com.pico.server.entity.Anniversary;
import com.pico.server.entity.Letter;
import com.pico.server.entity.Memorybox;
import com.pico.server.entity.Photo;
import com.pico.server.entity.Schedule;
import com.pico.server.entity.Users;
import com.pico.server.exception.ErrorCode;
import com.pico.server.exception.MemoryboxException;
import com.pico.server.exception.ScheduleException;
import com.pico.server.exception.UserException;
import com.pico.server.repository.LetterRepository;
import com.pico.server.repository.MemoryboxRepository;
import com.pico.server.repository.PhotoRepository;
import com.pico.server.repository.ScheduleRepository;
import com.pico.server.repository.UserRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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
    private final LetterRepository letterRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final LetterService letterService;
    private final PhotoService photoService;

    private final String PHOTO_PREFIX = "PHOTO_OPEN";
    private final String USER_PREFIX = "USER_";
    private final String SCHEDULE_PREFIX = "SCHEDULE_START_";

    @Transactional
    public MemoryboxDto saveMemoryBox(Users user, Anniversary anniversary, MemoryboxRequest request)
        throws IOException {
        if(memoryboxRepository.existsByScheduleScheduleIdAndScheduleStartTimeAndScheduleEndTime(request.scheduleId(), request.scheduleStartTime(), request.scheduleEndTime())) {
            throw new MemoryboxException(ErrorCode.DUPLICATE_MEMORYBOX);
            //얘도 바꿔야함(userID로 구분되어야함)
        }

        Schedule schedule = scheduleRepository.findById(request.scheduleId()).orElseThrow(() -> new ScheduleException(
            ErrorCode.NOT_FOUND_SCHEDULE));
        LocalDateTime opendate = anniversary.getDate().atTime(LocalTime.of(9,0));
        //schedule로 변경
        String fileName = USER_PREFIX + user.getId().toString() + SCHEDULE_PREFIX + request.scheduleStartTime().toString() + PHOTO_PREFIX + opendate.toString();
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
    public MemoryboxDto updateMemorybox(Long userId, Long memoryboxId, MemoryboxUpdateRequest request)
        throws IOException {
        Memorybox memorybox = memoryboxRepository.findById(memoryboxId)
            .orElseThrow(() -> new MemoryboxException(ErrorCode.NOT_FOUND_MEMORYBOX));
        Schedule schedule = memorybox.getSchedule();
        Hibernate.initialize(schedule);

        if(request.letterId() != null) {
            Letter letter =letterService.findById(request.letterId());
            letter.updateContent(request.letter());
            letterRepository.save(letter);
        }

        if(request.photoId() != null) {
            Photo photo = photoService.findById(request.photoId());
            String fileName = USER_PREFIX + userId.toString() +SCHEDULE_PREFIX + memorybox.getScheduleStartTime().toString() +PHOTO_PREFIX + memorybox.getOpendate().toString();
            s3Service.deletePhotoMultipartImage(fileName);
            String url = s3Service.putPhotoMultipartImage(request.photo(), fileName);
            photo.updateUrl(url);
            photoRepository.save(photo);
        }
        return MemoryboxDto.of(memorybox, schedule);
    }

    @Transactional
    public void deleteMemorybox(Long memoryboxId) {
        Memorybox memorybox = memoryboxRepository.findById(memoryboxId)
            .orElseThrow(() -> new MemoryboxException(ErrorCode.NOT_FOUND_MEMORYBOX));
        memoryboxRepository.delete(memorybox);
    }

    @Transactional
    public void deleteAllMemoryBox(Long userId) {
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));
        memoryboxRepository.deleteByUserIdAndPartnerId(userId, user.getUserDetails().getPartnerId());
    }

    @Transactional(readOnly = true)
    public List<MemoryboxResponse> getAnniversaryMemoryboxes(Long userId, Long anniversaryId) {
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));

        List<MemoryboxResponse> memoryboxResponses = new ArrayList<>();
        List<Memorybox> memoryboxes = memoryboxRepository.findByUserIdAndPartnerIdAnniversaryId(userId, user.getUserDetails().getPartnerId() ,anniversaryId);
        for(Memorybox memorybox : memoryboxes) {
            List<LetterDto> letters = LetterDto.from(memorybox.getLetters());
            List<PhotoDto> photos = PhotoDto.from(memorybox.getPhotos());
            MemoryboxDto memoryboxDto = MemoryboxDto.of(memorybox, memorybox.getSchedule());
            memoryboxResponses.add(MemoryboxResponse.of(memoryboxDto, letters, photos));
        }
        return memoryboxResponses;
    }


    @Transactional(readOnly = true)
    public List<MemoryboxResponse> getAllMemoryboxes(Long userId) {
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));

        List<MemoryboxResponse> memoryboxResponses = new ArrayList<>();
        List<Memorybox> memoryboxes = memoryboxRepository.findByUserIdAndPartnerId(userId, user.getUserDetails().getPartnerId());
        for(Memorybox memorybox : memoryboxes) {
            List<LetterDto> letters = LetterDto.from(memorybox.getLetters());
            List<PhotoDto> photos = PhotoDto.from(memorybox.getPhotos());
            MemoryboxDto memoryboxDto = MemoryboxDto.of(memorybox, memorybox.getSchedule());
            memoryboxResponses.add(MemoryboxResponse.of(memoryboxDto, letters, photos));
        }
        return memoryboxResponses;
    }

}
