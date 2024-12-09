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
import com.pico.server.repository.RepeatInfoRepository;
import com.pico.server.repository.ScheduleRepository;
import com.pico.server.repository.UserRepository;
import java.time.LocalDateTime;
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
            repeatInfo = createRepeatInfo(createScheduleDto.startTime(), createScheduleDto.repeat());
            repeatInfoRepository.save(repeatInfo);
        }

        Schedule schedule = Schedule.builder()
            .user(user)
            .title(createScheduleDto.title())
            .startTime(createScheduleDto.startTime())
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
        RepeatInfo repeatInfo = schedule.getRepeatInfo();
        repeatInfo.updateRepeatEndDate(repeatEndDate);
        repeatInfoRepository.save(repeatInfo);
        return ScheduleDto.from(schedule);
    }

    @Transactional
    public ScheduleDto updateSchedule(Long userId, Long scheduleId, UpdateScheduleDto updateDto) {
        Schedule schedule = scheduleRepository.findByUserIdAndScheduleId(userId, scheduleId)
            .orElseThrow(() -> new ScheduleException(ErrorCode.NOT_FOUND_SCHEDULE));

        RepeatInfo repeatInfo = schedule.getRepeatInfo();
        if (Boolean.TRUE.equals(updateDto.isRepeat())) {
            repeatInfo.updateRepeatInfo(updateDto.repeat(),updateDto.startTime());
            repeatInfoRepository.save(repeatInfo);
        } else {
            repeatInfoRepository.delete(repeatInfo);
            repeatInfo = null;
        }

        Schedule updatedSchedule = Schedule.builder()
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
    public List<ScheduleDto> getAllSchedule(Long userId) {
        List<ScheduleDto> scheduleDtos = new ArrayList<>();
        List<Schedule> schedules = scheduleRepository.findByUserId(userId);
        for(Schedule schedule : schedules) {
            scheduleDtos.add(ScheduleDto.from(schedule));
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
    public List<ScheduleDto> getTodaySchedules(Long userId, LocalDateTime todayDate) {
        List<ScheduleDto> scheduleDtos = new ArrayList<>();
        List<Schedule> schedules = scheduleRepository.findSchedulesByUserIdAndTodayDate(userId, todayDate);
        for(Schedule schedule : schedules) {
            scheduleDtos.add(ScheduleDto.from(schedule));
        }
        return scheduleDtos;
    }

    @Transactional(readOnly = true)
    public List<ScheduleDto> getSixMonthsSchedules(Long userId, String year, boolean isStart) {
        int yearInt;
        try {
            yearInt = Integer.parseInt(year);
        } catch (NumberFormatException e) {
            throw new ScheduleException(ErrorCode.INVALID_INPUT_YEAR_VALUE);
        }

        LocalDateTime startDate;
        LocalDateTime endDate;
        List<ScheduleDto> scheduleDtos = new ArrayList<>();

        if (isStart) {
            startDate = LocalDateTime.of(yearInt, 1, 1, 0, 0);
            endDate = LocalDateTime.of(yearInt, 6, 30, 23, 59);
        } else {
            startDate = LocalDateTime.of(yearInt, 7, 1, 0, 0);
            endDate = LocalDateTime.of(yearInt, 12, 31, 23, 59);
        }

        List<Schedule> schedules = scheduleRepository.findSchedulesByUserIdAndDateRange(userId, startDate, endDate);
        for(Schedule schedule : schedules) {
            scheduleDtos.add(ScheduleDto.from(schedule));
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




}
