package com.pico.server.service;

import com.pico.server.dto.LetterDto;
import com.pico.server.dto.MemoryUserDto;
import com.pico.server.dto.MemoryboxDto;
import com.pico.server.dto.PhotoDto;
import com.pico.server.dto.request.MemoryboxRequest;
import com.pico.server.dto.request.MemoryboxUpdateRequest;
import com.pico.server.dto.response.MemoryboxResponse;
import com.pico.server.dto.response.MemoryboxScheduleResponse;
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
import java.time.LocalDate;
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
    public MemoryboxDto saveMemoryBox(Users user, MemoryboxRequest request)
        throws IOException {
        if(memoryboxRepository.existsByScheduleScheduleIdAndScheduleUserIdAndScheduleStartTimeAndScheduleEndTime(request.scheduleId(), user.getId(),request.scheduleStartTime(), request.scheduleEndTime())) {
            throw new MemoryboxException(ErrorCode.DUPLICATE_MEMORYBOX);
        }

        Schedule schedule = scheduleRepository.findById(request.scheduleId()).orElseThrow(() -> new ScheduleException(
            ErrorCode.NOT_FOUND_SCHEDULE));

        if(schedule.getIsAnniversary().equals(Boolean.FALSE)) {
            throw new ScheduleException(ErrorCode.NOT_ANNIVERSARY_SCHEDULE);
        }

        LocalDateTime scheduleDate = schedule.getStartTime();
        LocalDateTime opendate = LocalDateTime.of(scheduleDate.getYear(), scheduleDate.getMonth(), scheduleDate.getDayOfMonth(), 9,0,0);
        Boolean isOpen = !LocalDateTime.now().isBefore(opendate);

        String fileName = USER_PREFIX + user.getId().toString() + SCHEDULE_PREFIX + request.scheduleStartTime().toString() + PHOTO_PREFIX + opendate.toString();
        String url = s3Service.putPhotoMultipartImage(request.photo(),fileName);

        Memorybox memorybox =  Memorybox.builder()
            .scheduleStartTime(request.scheduleStartTime())
            .scheduleEndTime(request.scheduleEndTime())
            .opendate(opendate)
            .user(user)
            .schedule(schedule)
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

        return MemoryboxDto.of(memorybox, isOpen, schedule);
    }

    @Transactional
    public MemoryboxDto updateMemorybox(Long userId, Long memoryboxId, MemoryboxUpdateRequest request)
        throws IOException {
        Memorybox memorybox = memoryboxRepository.findById(memoryboxId)
            .orElseThrow(() -> new MemoryboxException(ErrorCode.NOT_FOUND_MEMORYBOX));
        Schedule schedule = memorybox.getSchedule();
        Hibernate.initialize(schedule);

        LocalDate openDate = memorybox.getOpendate().toLocalDate();
        if(LocalDate.now().isAfter(openDate)) {
            throw new MemoryboxException(ErrorCode.NOT_BEFORE_OPENDATE);
        }
        Boolean isOpen = !LocalDateTime.now().isBefore(memorybox.getOpendate());

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
        return MemoryboxDto.of(memorybox,isOpen, schedule);
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
        memoryboxRepository.deleteAllByUserId(userId);
    }

    @Transactional(readOnly = true)
    public MemoryUserDto findMemoryboxUser(Long memoryboxId) {
        Memorybox memorybox = memoryboxRepository.findById(memoryboxId)
            .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_MEMORYBOX));
        return MemoryUserDto.from(memorybox.getUser(), memorybox.getUser().getUserDetails());
    }

    @Transactional(readOnly = true)
    public List<MemoryboxScheduleResponse> getAllMemoryboxes(Long userId) {
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));
        Long partnerId = user.getUserDetails().getPartnerId();

        //isAnniversary인 일정 불러오기(내 id, 상대방 id)
        //해당 일정에 대해 forEach로 schedule에 해당하는 id로 memoryboxes찾기 -> List<MemoryboxResponse> 에 넣기
        //MemoryScheduleResponse -> schedule 이름, List<MemoryboxResponse>
        //응답 -> List<MemoryScheduleResponse>

        List<MemoryboxScheduleResponse> memoryboxScheduleResponses = new ArrayList<>();
        List<Schedule> anniversarySchedules = scheduleRepository.findByUserIdAndPartnerIdAndIsAnniversaryTrue(userId, partnerId);
        for(Schedule schedule : anniversarySchedules) {
            List<MemoryboxResponse> memoryboxResponses = new ArrayList<>();
            List<Memorybox> memoryboxes = memoryboxRepository.findByUserIdAndPartnerIdAndScheduleId(userId, partnerId, schedule.getScheduleId());
            for(Memorybox memorybox : memoryboxes) {
                List<LetterDto> letters = LetterDto.from(memorybox.getLetters());
                List<PhotoDto> photos = PhotoDto.from(memorybox.getPhotos());
                Boolean isOpen = !LocalDateTime.now().isBefore(memorybox.getOpendate());
                MemoryboxDto memoryboxDto = MemoryboxDto.of(memorybox,isOpen, memorybox.getSchedule());

                MemoryUserDto author = MemoryUserDto.from(memorybox.getUser(), memorybox.getUser()
                    .getUserDetails());
                memoryboxResponses.add(MemoryboxResponse.of(memoryboxDto, letters, photos,author));
            }
            memoryboxScheduleResponses.add(MemoryboxScheduleResponse.of(schedule.getTitle(), memoryboxResponses));
        }
        return memoryboxScheduleResponses;
    }

    @Transactional(readOnly = true)
    public List<MemoryboxResponse> getAnniversaryMemoryboxes(Long userId, Long scheduleId) {
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));

        List<MemoryboxResponse> memoryboxResponses = new ArrayList<>();
        List<Memorybox> memoryboxes = memoryboxRepository.findByUserIdAndPartnerIdAndScheduleId(userId, user.getUserDetails().getPartnerId() ,scheduleId);
        for(Memorybox memorybox : memoryboxes) {
            List<LetterDto> letters = LetterDto.from(memorybox.getLetters());
            List<PhotoDto> photos = PhotoDto.from(memorybox.getPhotos());
            Boolean isOpen = !LocalDateTime.now().isBefore(memorybox.getOpendate());
            MemoryboxDto memoryboxDto = MemoryboxDto.of(memorybox,isOpen, memorybox.getSchedule());

            MemoryUserDto author = MemoryUserDto.from(memorybox.getUser(), memorybox.getUser()
                .getUserDetails());
            memoryboxResponses.add(MemoryboxResponse.of(memoryboxDto, letters, photos,author));
        }
        return memoryboxResponses;
    }
    @Transactional(readOnly = true)
    public List<MemoryboxResponse> getMyPastMemoryboxes(Long userId) {
        List<MemoryboxResponse> memoryboxResponses = new ArrayList<>();
        List<Memorybox> memoryboxes = memoryboxRepository.findByUserIdAndOpendateIsBefore(userId, LocalDateTime.now());
        for(Memorybox memorybox : memoryboxes) {
            List<LetterDto> letters = LetterDto.from(memorybox.getLetters());
            List<PhotoDto> photos = PhotoDto.from(memorybox.getPhotos());
            Boolean isOpen = !LocalDateTime.now().isBefore(memorybox.getOpendate());
            MemoryboxDto memoryboxDto = MemoryboxDto.of(memorybox,isOpen, memorybox.getSchedule());

            MemoryUserDto author = MemoryUserDto.from(memorybox.getUser(), memorybox.getUser()
                .getUserDetails());
            memoryboxResponses.add(MemoryboxResponse.of(memoryboxDto, letters, photos,author));
        }
        return memoryboxResponses;
    }

    @Transactional(readOnly = true)
    public List<MemoryboxResponse> getMyUpcomingMemoryboxes(Long userId) {
        List<MemoryboxResponse> memoryboxResponses = new ArrayList<>();
        List<Memorybox> memoryboxes = memoryboxRepository.findByUserIdAndOpendateIsAfter(userId, LocalDateTime.now());
        for(Memorybox memorybox : memoryboxes) {
            List<LetterDto> letters = LetterDto.from(memorybox.getLetters());
            List<PhotoDto> photos = PhotoDto.from(memorybox.getPhotos());
            Boolean isOpen = !LocalDateTime.now().isBefore(memorybox.getOpendate());
            MemoryboxDto memoryboxDto = MemoryboxDto.of(memorybox,isOpen, memorybox.getSchedule());

            MemoryUserDto author = MemoryUserDto.from(memorybox.getUser(), memorybox.getUser()
                .getUserDetails());
            memoryboxResponses.add(MemoryboxResponse.of(memoryboxDto, letters, photos,author));
        }
        return memoryboxResponses;
    }

}
