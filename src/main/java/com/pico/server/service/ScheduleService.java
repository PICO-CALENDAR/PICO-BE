package com.pico.server.service;

import com.pico.server.dto.ScheduleDto;
import com.pico.server.dto.request.CreateScheduleDto;
import com.pico.server.dto.request.UpdateScheduleDto;
import com.pico.server.entity.Anniversary;
import com.pico.server.entity.RepeatInfo;
import com.pico.server.entity.Schedule;
import com.pico.server.entity.Users;
import com.pico.server.enums.RepeatType;
import com.pico.server.enums.ScheduleType;
import com.pico.server.exception.ErrorCode;
import com.pico.server.exception.ScheduleException;
import com.pico.server.exception.UserException;
import com.pico.server.repository.AnniversaryRepository;
import com.pico.server.repository.RepeatInfoRepository;
import com.pico.server.repository.ScheduleRepository;
import com.pico.server.repository.UserRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final RepeatInfoRepository repeatInfoRepository;
    private final UserRepository userRepository;
    private final AnniversaryRepository anniversaryRepository;

    @Transactional
    public ScheduleDto createSchedule(Long userId, CreateScheduleDto createScheduleDto) {
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new ScheduleException(ErrorCode.NOT_FOUND_USER));

        RepeatInfo repeatInfo = null;
        if (createScheduleDto.isRepeat()) {
            repeatInfo = createRepeatInfo(createScheduleDto.startTime(), createScheduleDto.repeatType());
            repeatInfoRepository.save(repeatInfo);
        }

        Schedule schedule = Schedule.builder()
            .user(user)
            .title(createScheduleDto.title())
            .startTime(createScheduleDto.startTime())
            .category(createScheduleDto.category())
            .endTime(createScheduleDto.endTime())
            .isAllDay(createScheduleDto.isAllDay())
            .meetingPeople(createScheduleDto.meetingPeople())
            .isRepeat(createScheduleDto.isRepeat())
            .isAnniversary(false)
            .isCoupleAnniversary(false)
            .repeatInfo(repeatInfo)
            .build();

        scheduleRepository.save(schedule);
        return ScheduleDto.from(schedule);
    }

    @Transactional
    public void createBasicSchedules(Long userId) {
        List<ScheduleDto> scheduleDtos = new ArrayList<>();

        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));

        LocalDateTime birthdayDate = user.getUserDetails().getBirth().atTime(0,0,0);
        RepeatInfo birthDayRepeatInfo = createRepeatInfo(birthdayDate, RepeatType.YEARLY);
        Schedule birthday = Schedule.builder()
            .user(user)
            .title(user.getUserDetails().getName() + "님의 생일")
            .category(ScheduleType.MINE)
            .startTime(birthdayDate)
            .endTime(birthdayDate)
            .isAllDay(true)
            .isRepeat(true)
            .isAnniversary(false)
            .isCoupleAnniversary(false)
            .repeatInfo(birthDayRepeatInfo)
            .build();
        scheduleRepository.save(birthday);
    }
    @Transactional
    public void createBasicAnniversarySchedules(Long userId) {
        List<Schedule> schedules = new ArrayList<>();
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));

        List<Anniversary> anniversaries = anniversaryRepository.findAll();
        for(Anniversary anniversary : anniversaries) {
            Schedule schedule = Schedule.builder()
                .user(user)
                .title(anniversary.getTitle())
                .category(ScheduleType.MINE)
                .startTime(anniversary.getDate().atTime(0,0,0).withYear(2000))
                .endTime(anniversary.getDate().atTime(0,0,0).withYear(2000))
                .isAllDay(true)
                .isRepeat(true)
                .isAnniversary(true)
                .isCoupleAnniversary(false)
                .anniversary(anniversary)
                .repeatInfo(createYearlyRepeatInfo(anniversary.getDate().atTime(0,0,0).withYear(2000)))
                .build();

            schedules.add(schedule);
        }


        scheduleRepository.saveAll(schedules);
    }

    @Transactional
    public void createAnniversarySchedules(Long userId) {
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));

        String myName = user.getUserDetails().getName();
        String partnerName = user.getUserDetails().getPartnerName();

        LocalDateTime anniversaryDate = user.getUserDetails().getDday().atTime(0,0,0);
        RepeatInfo anniverSaryRepeatInfo = createRepeatInfo(anniversaryDate, RepeatType.YEARLY);
        Schedule anniversary = Schedule.builder()
            .user(user)
            .title(myName + "과 " + partnerName+"가 만난 날")
            .category(ScheduleType.OURS)
            .startTime(anniversaryDate)
            .endTime(anniversaryDate)
            .isAllDay(true)
            .isRepeat(true)
            .isAnniversary(true)
            .isCoupleAnniversary(true)
            .repeatInfo(anniverSaryRepeatInfo)
            .build();
        scheduleRepository.save(anniversary);


        List<Schedule> schedules = List.of(
            createAnniversaryNotRepeat(user,100L, anniversaryDate),
            createAnniversaryNotRepeat(user,200L, anniversaryDate),
            createAnniversaryNotRepeat(user,300L, anniversaryDate)
        );
        scheduleRepository.saveAll(schedules);
    }

    @Transactional
    public ScheduleDto deleteSchedule(Long userId, Long scheduleId) {
        Schedule schedule = scheduleRepository.findByUserIdAndScheduleId(userId, scheduleId)
            .orElseThrow(() -> new ScheduleException(ErrorCode.NOT_FOUND_SCHEDULE));
        scheduleRepository.delete(schedule);
        return ScheduleDto.from(schedule);
    }

    @Transactional
    public ScheduleDto deleteRepeatSchedule(Long userId, Long scheduleId, LocalDateTime repeatEndDate) {
        Schedule schedule = scheduleRepository.findByUserIdAndScheduleId(userId, scheduleId)
            .orElseThrow(() -> new ScheduleException(ErrorCode.NOT_FOUND_SCHEDULE));

        if(Boolean.FALSE.equals(schedule.getIsRepeat())) {
            throw new ScheduleException(ErrorCode.NOT_REPEAT_SCHEDULE);
        }
        LocalDateTime adjustedRepeatEndDate = repeatEndDate.minusDays(1).with(LocalTime.MAX);
        RepeatInfo repeatInfo = schedule.getRepeatInfo();
        repeatInfo.updateRepeatEndDate(adjustedRepeatEndDate);
        repeatInfoRepository.save(repeatInfo);
        schedule.updateRepeatInfo(repeatInfo);
        scheduleRepository.save(schedule);
        return ScheduleDto.from(schedule);
    }

    @Transactional
    public void deleteAnniversarySchedules(Long userId) {
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));
        Long partnerId = user.getUserDetails().getPartnerId();
        scheduleRepository.deleteAnniversarySchedulesByUserIdOrPartnerId(userId, partnerId);
    }

    @Transactional
    public ScheduleDto updateOnlyTodaySchedule(Long userId, Long scheduleId, UpdateScheduleDto updateDto) {
        //original schedule entTime 변경
        Schedule schedule = scheduleRepository.findByUserIdAndScheduleId(userId, scheduleId)
            .orElseThrow(() -> new ScheduleException(ErrorCode.NOT_FOUND_SCHEDULE));

        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));

        RepeatInfo originalRepeatInfo = schedule.getRepeatInfo();
        originalRepeatInfo.updateRepeatEndDate(LocalDateTime.now().minusDays(1).with(LocalTime.of(23,59)));
        repeatInfoRepository.save(originalRepeatInfo);
        scheduleRepository.save(schedule);

        //변경된 schedule 생성
        RepeatInfo changedRepeatInfo = createRepeatInfo(updateDto.startTime(), updateDto.repeatType());
        Schedule changedSchdule = Schedule.builder()
            .user(user)
            .title(updateDto.title())
            .category(updateDto.category())
            .startTime(updateDto.startTime())
            .endTime(updateDto.endTime())
            .isAllDay(updateDto.isAllDay())
            .isRepeat(updateDto.isRepeat())
            .isAnniversary(false)
            .isCoupleAnniversary(false)
            .meetingPeople(updateDto.meetingPeople())
            .repeatInfo(changedRepeatInfo)
            .build();
        repeatInfoRepository.save(changedRepeatInfo);
        scheduleRepository.save(changedSchdule);

        //new Origin 스케줄 생성
        LocalDateTime newStartTime = checkNextStartTime(originalRepeatInfo);
        RepeatInfo repeatInfo = createRepeatInfo(newStartTime, originalRepeatInfo.getRepeatType());
        Schedule newOriginSchedule = makeOriginSchedule(schedule, repeatInfo, newStartTime);
        repeatInfoRepository.save(repeatInfo);
        scheduleRepository.save(newOriginSchedule);

        return ScheduleDto.from(changedSchdule);
    }

    @Transactional
    public ScheduleDto updateAfterTodaySchedule(Long userId, Long scheduleId, UpdateScheduleDto updateDto) {
        //original schedule entTime 변경
        Schedule schedule = scheduleRepository.findByUserIdAndScheduleId(userId, scheduleId)
            .orElseThrow(() -> new ScheduleException(ErrorCode.NOT_FOUND_SCHEDULE));

        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));

        RepeatInfo originalRepeatInfo = schedule.getRepeatInfo();
        originalRepeatInfo.updateRepeatEndDate(LocalDateTime.now().minusDays(1).with(LocalTime.of(23,59)));
        repeatInfoRepository.save(originalRepeatInfo);
        scheduleRepository.save(schedule);

        //변경된 schedule 생성
        RepeatInfo changedRepeatInfo = createRepeatInfo(updateDto.startTime(), updateDto.repeatType());
        Schedule changedSchdule = Schedule.builder()
            .user(user)
            .title(updateDto.title())
            .category(updateDto.category())
            .startTime(updateDto.startTime())
            .endTime(updateDto.endTime())
            .isAllDay(updateDto.isAllDay())
            .isRepeat(updateDto.isRepeat())
            .isAnniversary(false)
            .isCoupleAnniversary(false)
            .meetingPeople(updateDto.meetingPeople())
            .repeatInfo(changedRepeatInfo)
            .build();
        repeatInfoRepository.save(changedRepeatInfo);
        scheduleRepository.save(changedSchdule);

        return ScheduleDto.from(changedSchdule);
    }

    @Transactional
    public ScheduleDto updateSchedule(Long userId, Long scheduleId, UpdateScheduleDto updateDto) {
        Schedule schedule = scheduleRepository.findByUserIdAndScheduleId(userId, scheduleId)
            .orElseThrow(() -> new ScheduleException(ErrorCode.NOT_FOUND_SCHEDULE));

        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));

        RepeatInfo repeatInfo = schedule.getRepeatInfo();
        if(schedule.getRepeatInfo() !=null) {
            if (Boolean.TRUE.equals(updateDto.isRepeat())) {
                repeatInfo.updateRepeatInfo(updateDto.repeatType(),updateDto.startTime());
                repeatInfoRepository.save(repeatInfo);
            } else {
                repeatInfoRepository.delete(repeatInfo);
                repeatInfo = null;
            }
        } else {
            repeatInfo = createRepeatInfo(updateDto.startTime(), updateDto.repeatType());
            repeatInfoRepository.save(repeatInfo);
        }

        Schedule updatedSchedule = Schedule.builder()
            .user(user)
            .scheduleId(schedule.getScheduleId())
            .title(updateDto.title())
            .category(updateDto.category())
            .startTime(updateDto.startTime())
            .endTime(updateDto.endTime())
            .isAllDay(updateDto.isAllDay())
            .isRepeat(updateDto.isRepeat())
            .isAnniversary(false)
            .isCoupleAnniversary(false)
            .meetingPeople(updateDto.meetingPeople())
            .repeatInfo(repeatInfo)
            .build();

        scheduleRepository.save(updatedSchedule);
        return ScheduleDto.from(updatedSchedule);
    }

    @Transactional(readOnly = true)
    public List<ScheduleDto> getThreeMonthsAnniversarySchedules(Long userId) {
        List<ScheduleDto> scheduleDtos = new ArrayList<>();
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));

        Long partnerId = user.getUserDetails().getPartnerId();
        LocalDate afterThreeMonths = LocalDate.now().plusMonths(3);
        LocalDate now = LocalDate.now();
        List<Schedule> schedules = scheduleRepository.findThreeMonthsAnniversarys(now, afterThreeMonths, userId, partnerId);
        //TODO: Batch 서버에서 anniversary 기간 지날때마다 update 로직 구현 필요
        for(Schedule schedule : schedules) {
            LocalDateTime updatedTime = schedule.getStartTime()
                .withYear(schedule.getAnniversary().getDate().getYear())
                .withMonth(schedule.getAnniversary().getDate().getMonthValue())
                .withDayOfMonth(schedule.getAnniversary().getDate().getDayOfMonth());

            scheduleDtos.add(ScheduleDto.of(schedule, updatedTime));
        }
        return scheduleDtos;
    }

    @Transactional(readOnly = true)
    public ScheduleDto getOneSchedule(Long userId, Long scheduleId) {
        Schedule schedule = scheduleRepository.findByUserIdAndScheduleId(userId,scheduleId)
            .orElseThrow(() -> new ScheduleException(ErrorCode.NOT_FOUND_SCHEDULE));
        return ScheduleDto.from(schedule);
    }

    @Transactional(readOnly = true)
    public List<ScheduleDto> getWeekSchedules(Long userId, LocalDateTime todayDate) {
        List<ScheduleDto> scheduleDtos = new ArrayList<>();
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new ScheduleException(ErrorCode.NOT_FOUND_USER));

        LocalDateTime weekStart = todayDate.with(DayOfWeek.MONDAY).with(LocalTime.MIN);
        LocalDateTime weekEnd = todayDate.with(DayOfWeek.SUNDAY).with(LocalTime.MAX);

        //사용자 일정 조회
        List<Schedule> schedules = scheduleRepository.findSchedulesByUserIdAndDateRangeAndIsRepeatNot(userId, weekStart, weekEnd);
        for(Schedule schedule : schedules) {
            scheduleDtos.add(ScheduleDto.from(schedule));
        }

        //사용자 반복 일정 확인 및 추가
        List<Schedule> recursiveSchedules = scheduleRepository.findSchedulesByUserIdAndIsRepeat(userId);
        for (Schedule recurringSchedule : recursiveSchedules) {
            if (isRecurringScheduleWithinRange(recurringSchedule, weekStart, weekEnd)) {
                scheduleDtos.add(ScheduleDto.from(recurringSchedule));
            }
        }

        //파트너 일정 조회
        if(user.getUserDetails().getPartnerId()!= null) {
            List<Schedule> partnerSchedules = scheduleRepository.findSchedulesByUserIdAndDateRangeAndIsRepeatNot(user.getUserDetails().getPartnerId(), weekStart, weekEnd);
            for(Schedule schedule : partnerSchedules) {
                scheduleDtos.add(ScheduleDto.fromPartner(schedule));
            }

            List<Schedule> partnerRecursiveSchedules = scheduleRepository.findPartnerSchedules(user.getUserDetails().getPartnerId());
            for(Schedule schedule : partnerRecursiveSchedules) {
                if (isRecurringScheduleWithinRange(schedule, weekStart, weekEnd)) {
                    scheduleDtos.add(ScheduleDto.from(schedule));
                }
            }
        }

        return scheduleDtos;
    }

    @Transactional(readOnly = true)
    public List<ScheduleDto> getYearSchedules(Long userId, String year) {
        List<ScheduleDto> scheduleDtos = new ArrayList<>();
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new ScheduleException(ErrorCode.NOT_FOUND_USER));
        int yearInt;
        try {
            yearInt = Integer.parseInt(year);
        } catch (NumberFormatException e) {
            throw new ScheduleException(ErrorCode.INVALID_INPUT_YEAR_VALUE);
        }
        LocalDateTime startDate = LocalDateTime.of(yearInt, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(yearInt, 12, 31, 23, 59);

        //사용자 일정 조회
        List<Schedule> schedules = scheduleRepository.findSchedulesByUserIdAndDateRangeAndIsRepeatNot(userId, startDate, endDate);
        for(Schedule schedule : schedules) {
            scheduleDtos.add(ScheduleDto.from(schedule));
        }
        //사용자 반복 일정 확인 및 추가
        List<Schedule> recursiveSchedules = scheduleRepository.findSchedulesByUserIdAndIsRepeat(userId);
        for(Schedule recursiveSchedule : recursiveSchedules) {
            scheduleDtos.add(ScheduleDto.from(recursiveSchedule));
        }

        //파트너 일정 조회
        if(user.getUserDetails().getPartnerId()!= null) {
            List<Schedule> partnerSchedules = scheduleRepository.findSchedulesByUserIdAndDateRangeAndIsRepeatNot(user.getUserDetails().getPartnerId(), startDate, endDate);
            for(Schedule schedule : partnerSchedules) {
                scheduleDtos.add(ScheduleDto.fromPartner(schedule));
            }

            List<Schedule> partnerRecursiveSchedules = scheduleRepository.findPartnerSchedules(user.getUserDetails().getPartnerId());
            for(Schedule schedule : partnerRecursiveSchedules) {
                scheduleDtos.add(ScheduleDto.fromPartner(schedule));
            }
        }
        return scheduleDtos;
    }

    private RepeatInfo createRepeatInfo(LocalDateTime startTime, RepeatType repeatType) {
        return RepeatInfo.builder()
            .repeatType(repeatType)
            .repeatStartDate(startTime)
            .repeatEndDate(null)
            .build();
    }

    private RepeatInfo createYearlyRepeatInfo(LocalDateTime startTime) {
        return RepeatInfo.builder()
            .repeatType(RepeatType.YEARLY)
            .repeatStartDate(startTime)
            .repeatEndDate(null)
            .build();
    }


    private boolean isRecurringScheduleWithinRange(Schedule schedule, LocalDateTime weekStart, LocalDateTime weekEnd) {
        RepeatInfo repeatInfo = schedule.getRepeatInfo();
        if (repeatInfo == null) {
            return false;
        }

        LocalDateTime repeatStartDate = repeatInfo.getRepeatStartDate();
        LocalDateTime repeatEndDate = repeatInfo.getRepeatEndDate();
        LocalDateTime effectiveEndDate = (repeatEndDate == null || repeatEndDate.isAfter(weekEnd))
            ? weekEnd
            : repeatEndDate;

        RepeatType repeatType = repeatInfo.getRepeatType();
        LocalDateTime currentDate = repeatStartDate;

        while (!currentDate.isAfter(effectiveEndDate)) {
            if (!currentDate.isBefore(weekStart) && !currentDate.isAfter(weekEnd)) {
                return true;
            }

            switch (repeatType) {
                case DAILY:
                    currentDate = currentDate.plusDays(1);
                    break;
                case WEEKLY:
                    currentDate = currentDate.plusWeeks(1);
                    break;
                case MONTHLY:
                    currentDate = currentDate.plusMonths(1);
                    break;
                case YEARLY:
                    currentDate = currentDate.plusYears(1);
                    break;
                case BIWEEKLY:
                    currentDate = currentDate.plusWeeks(2);
                    break;
                default:
                    throw new ScheduleException(ErrorCode.NOT_FOUND_SCHEDULE);
            }
        }

        return false;
    }

    private Schedule makeOriginSchedule(Schedule schedule, RepeatInfo repeatInfo, LocalDateTime startTime) {
        return Schedule.builder()
            .user(schedule.getUser())
            .title(schedule.getTitle())
            .category(schedule.getCategory())
            .startTime(startTime)
            .endTime(schedule.getEndTime())
            .isAllDay(schedule.getIsAllDay())
            .isRepeat(schedule.getIsRepeat())
            .isAnniversary(false)
            .isCoupleAnniversary(false)
            .meetingPeople(schedule.getMeetingPeople())
            .repeatInfo(repeatInfo)
            .build();
    }

    private LocalDateTime checkNextStartTime(RepeatInfo repeatInfo) {
        RepeatType repeatType = repeatInfo.getRepeatType();
        LocalDateTime currentDate = repeatInfo.getRepeatStartDate();

        switch (repeatType) {
            case DAILY:
                currentDate = currentDate.plusDays(1);
                break;
            case WEEKLY:
                currentDate = currentDate.plusWeeks(1);
                break;
            case MONTHLY:
                currentDate = currentDate.plusMonths(1);
                break;
            case YEARLY:
                currentDate = currentDate.plusYears(1);
                break;
            case BIWEEKLY:
                currentDate = currentDate.plusWeeks(2);
                break;
            default:
                throw new ScheduleException(ErrorCode.NOT_FOUND_SCHEDULE);
        }
        return currentDate;
    }

    private Schedule createAnniversaryNotRepeat(Users user, Long days, LocalDateTime anniversaryDate) {
        return Schedule.builder()
            .user(user)
            .title("만난지 "+ days.toString()+"일 째")
            .category(ScheduleType.OURS)
            .startTime(anniversaryDate.plusDays(days-1))
            .endTime(anniversaryDate.plusDays(days-1))
            .isAllDay(true)
            .isRepeat(false)
            .isAnniversary(true)
            .isCoupleAnniversary(true)
            .repeatInfo(null)
            .build();
    }

}
