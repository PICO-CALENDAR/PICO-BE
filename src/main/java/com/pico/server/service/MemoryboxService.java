package com.pico.server.service;

import com.pico.server.dto.LetterDto;
import com.pico.server.dto.MemoryUserDto;
import com.pico.server.dto.MemoryboxDto;
import com.pico.server.dto.PhotoDto;
import com.pico.server.dto.request.MemoryboxRequest;
import com.pico.server.dto.request.MemoryboxUpdateRequest;
import com.pico.server.dto.response.MemoryboxResponse;
import com.pico.server.dto.response.MemoryboxScheduleResponse;
import com.pico.server.entity.Anniversary;
import com.pico.server.entity.Letter;
import com.pico.server.entity.Memorybox;
import com.pico.server.entity.Photo;
import com.pico.server.entity.Schedule;
import com.pico.server.entity.Users;
import com.pico.server.exception.ErrorCode;
import com.pico.server.exception.LetterException;
import com.pico.server.exception.MemoryboxException;
import com.pico.server.exception.ScheduleException;
import com.pico.server.exception.UserException;
import com.pico.server.repository.AnniversaryRepository;
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
    private final AnniversaryRepository anniversaryRepository;
    private final S3Service s3Service;
    private final LetterService letterService;
    private final PhotoService photoService;
    private final UserService userService;

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
            .title(request.letterTitle())
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
            if(request.letterTitle() != null && request.letterTitle().length() > 16) {
                throw new LetterException(ErrorCode.MAX_LENGTH_OVER);
            }
            Letter letter =letterService.findById(request.letterId());
            letter.updateContent(request.letterTitle(),request.letter());
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
        MemoryUserDto partnerInfo = userService.findPartner(userId);

        //1. 전체 기념일을 조회한다
        //2. 기념일 List에서 각 기념일 마다 for each 구문을 실행 해 해당 title,userId,partnerId를 활용해 일정 List를 찾는다
        //3. 해당 일정 List에서 for each 구문 활용해 Memorybox List를 찾는다
        //4. 찾는데 홯용한 기념일 Title, memoryboxResponse 2개를 활용해 MemoryScheduleResponse를 하나 만든다.
        //5. 해당 내용을 기념일 전체에 진행해 List<MemoryScheduleResponse>를 만든다.

        List<MemoryboxScheduleResponse> memoryboxScheduleResponses = new ArrayList<>();
        List<Anniversary> anniversaries = anniversaryRepository.findAll();
        for(Anniversary anniversary : anniversaries) {
            String title = anniversary.getTitle();

            List<Schedule> schedules = scheduleRepository.findByTitleAndUserIdAndPartnerId(title, userId, partnerId);
            List<Memorybox> memoryboxes = memoryboxRepository.findBySchedules(schedules);

            List<MemoryboxResponse> memoryboxResponses = new ArrayList<>();
            for(Memorybox memorybox : memoryboxes) {
                List<LetterDto> letters = LetterDto.from(memorybox.getLetters());
                List<PhotoDto> photos = PhotoDto.from(memorybox.getPhotos());
                Boolean isOpen = !LocalDateTime.now().isBefore(memorybox.getOpendate());
                MemoryboxDto memoryboxDto = MemoryboxDto.of(memorybox,isOpen, memorybox.getSchedule());

                MemoryUserDto author = MemoryUserDto.from(memorybox.getUser(), memorybox.getUser()
                    .getUserDetails());
                memoryboxResponses.add(MemoryboxResponse.of(memoryboxDto, letters, photos,author,partnerInfo));
            }
            memoryboxScheduleResponses.add(MemoryboxScheduleResponse.of(title, memoryboxResponses));
        }
        return memoryboxScheduleResponses;
    }

    @Transactional(readOnly = true)
    public MemoryboxScheduleResponse getAnniversaryMemoryboxes(Long userId, String title) {
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));
        Long partnerId = user.getUserDetails().getPartnerId();
        MemoryUserDto partnerInfo = userService.findPartner(userId);

        List<Schedule> schedules = scheduleRepository.findByTitleAndUserIdAndPartnerId(title, userId, partnerId);
        List<Memorybox> memoryboxes = memoryboxRepository.findBySchedules(schedules);

        List<MemoryboxResponse> memoryboxResponses = new ArrayList<>();
        for(Memorybox memorybox : memoryboxes) {
            List<LetterDto> letters = LetterDto.from(memorybox.getLetters());
            List<PhotoDto> photos = PhotoDto.from(memorybox.getPhotos());
            Boolean isOpen = !LocalDateTime.now().isBefore(memorybox.getOpendate());
            MemoryboxDto memoryboxDto = MemoryboxDto.of(memorybox,isOpen, memorybox.getSchedule());

            MemoryUserDto author = MemoryUserDto.from(memorybox.getUser(), memorybox.getUser()
                .getUserDetails());
            memoryboxResponses.add(MemoryboxResponse.of(memoryboxDto, letters, photos,author, partnerInfo));
        }
        return MemoryboxScheduleResponse.of(title, memoryboxResponses);
    }
    @Transactional(readOnly = true)
    public List<MemoryboxResponse> getMyPastMemoryboxes(Long userId) {
        List<MemoryboxResponse> memoryboxResponses = new ArrayList<>();
        List<Memorybox> memoryboxes = memoryboxRepository.findByUserIdAndOpendateIsBefore(userId, LocalDateTime.now());
        MemoryUserDto partnerInfo = userService.findPartner(userId);
        for(Memorybox memorybox : memoryboxes) {
            List<LetterDto> letters = LetterDto.from(memorybox.getLetters());
            List<PhotoDto> photos = PhotoDto.from(memorybox.getPhotos());
            Boolean isOpen = !LocalDateTime.now().isBefore(memorybox.getOpendate());
            MemoryboxDto memoryboxDto = MemoryboxDto.of(memorybox,isOpen, memorybox.getSchedule());

            MemoryUserDto author = MemoryUserDto.from(memorybox.getUser(), memorybox.getUser()
                .getUserDetails());
            memoryboxResponses.add(MemoryboxResponse.of(memoryboxDto, letters, photos,author, partnerInfo));
        }
        return memoryboxResponses;
    }

    @Transactional(readOnly = true)
    public List<MemoryboxResponse> getMyUpcomingMemoryboxes(Long userId) {
        List<MemoryboxResponse> memoryboxResponses = new ArrayList<>();
        List<Memorybox> memoryboxes = memoryboxRepository.findByUserIdAndOpendateIsAfter(userId, LocalDateTime.now());
        MemoryUserDto partnerInfo = userService.findPartner(userId);
        for(Memorybox memorybox : memoryboxes) {
            List<LetterDto> letters = LetterDto.from(memorybox.getLetters());
            List<PhotoDto> photos = PhotoDto.from(memorybox.getPhotos());
            Boolean isOpen = !LocalDateTime.now().isBefore(memorybox.getOpendate());
            MemoryboxDto memoryboxDto = MemoryboxDto.of(memorybox,isOpen, memorybox.getSchedule());

            MemoryUserDto author = MemoryUserDto.from(memorybox.getUser(), memorybox.getUser()
                .getUserDetails());
            memoryboxResponses.add(MemoryboxResponse.of(memoryboxDto, letters, photos,author, partnerInfo));
        }
        return memoryboxResponses;
    }

}
