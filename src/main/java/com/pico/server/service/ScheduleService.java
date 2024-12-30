package com.pico.server.service;

import com.pico.server.dto.ScheduleDto;
import com.pico.server.dto.request.CreateScheduleDto;
import com.pico.server.dto.request.UpdateScheduleDto;
import com.pico.server.entity.RepeatInfo;
import com.pico.server.entity.Schedule;
import com.pico.server.entity.Users;
import com.pico.server.enums.RepeatType;
import com.pico.server.exception.ErrorCode;
import com.pico.server.exception.ScheduleException;
import com.pico.server.exception.UserException;
import com.pico.server.repository.RepeatInfoRepository;
import com.pico.server.repository.ScheduleRepository;
import com.pico.server.repository.UserRepository;
import java.time.DayOfWeek;
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
            .repeatInfo(repeatInfo)
            .build();

        scheduleRepository.save(schedule);
        return ScheduleDto.from(schedule);
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
        return ScheduleDto.from(schedule);
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
            .meetingPeople(updateDto.meetingPeople())
            .repeatInfo(repeatInfo)
            .build();

        scheduleRepository.save(updatedSchedule);
        return ScheduleDto.from(updatedSchedule);
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
        List<Schedule> schedules = scheduleRepository.findSchedulesByUserIdAndDateRange(userId, weekStart, weekEnd);
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
            List<Schedule> partnerSchedules = scheduleRepository.findSchedulesByUserIdAndDateRange(user.getUserDetails().getPartnerId(), weekStart, weekEnd);
            for(Schedule schedule : partnerSchedules) {
                scheduleDtos.add(ScheduleDto.fromPartner(schedule));
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
            List<Schedule> partnerSchedules = scheduleRepository.findSchedulesByUserIdAndDateRange(user.getUserDetails().getPartnerId(), startDate, endDate);
            for(Schedule schedule : partnerSchedules) {
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


}
