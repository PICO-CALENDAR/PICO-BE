package com.pico.server.service;

import com.pico.server.dto.ScheduleDto;
import com.pico.server.dto.request.CreateScheduleDto;
import com.pico.server.dto.request.UpdateScheduleDto;
import com.pico.server.entity.RepeatDay;
import com.pico.server.entity.RepeatInfo;
import com.pico.server.entity.Schedule;
import com.pico.server.entity.Users;
import com.pico.server.enums.RepeatDayType;
import com.pico.server.enums.RepeatType;
import com.pico.server.exception.ErrorCode;
import com.pico.server.exception.ScheduleException;
import com.pico.server.repository.RepeatDayRepository;
import com.pico.server.repository.RepeatInfoRepository;
import com.pico.server.repository.ScheduleRepository;
import com.pico.server.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final RepeatInfoRepository repeatInfoRepository;
    private final RepeatDayRepository repeatDayRepository;
    private final UserRepository userRepository;

    @Transactional
    public ScheduleDto createSchedule(Long userId, CreateScheduleDto createScheduleDto) {
        Users user = userRepository.findById(userId).get();
        RepeatInfo repeatInfo = null;
        if (createScheduleDto.isRepeat()) {
            repeatInfo = createRepeatInfo(createScheduleDto.repeat());
            if (createScheduleDto.repeat().needsRepeatDay()) {
                List<RepeatDay> repeatDays = createRepeatDays(repeatInfo, createScheduleDto.repeatDays());
                repeatDayRepository.saveAll(repeatDays);
            }
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
    public void deleteSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new ScheduleException(ErrorCode.NOT_FOUND_SCHEDULE));

        if (schedule.getRepeatInfo().getRepeatType().needsRepeatDay()) {
            repeatDayRepository.deleteByRepeatInfo(schedule.getRepeatInfo());
        }
        scheduleRepository.delete(schedule);
    }

    @Transactional
    public ScheduleDto updateSchedule(Long scheduleId, UpdateScheduleDto updateDto) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new ScheduleException(ErrorCode.NOT_FOUND_SCHEDULE));

        RepeatInfo repeatInfo = schedule.getRepeatInfo();
        LocalDateTime fixtedTime = updateDto.startTime();
        repeatDayRepository.deleteByRepeatInfo(repeatInfo);

        if (updateDto.isRepeat()) {
            if(updateDto.repeat().needsRepeatDay()) {
                createRepeatDays(repeatInfo, updateDto.repeatDays());
            }
            repeatInfo.updateRepeatInfo(updateDto.repeat(), fixtedTime); //반복 시간 이렇게 정하는거 맞나? 이렇게 하는거 맞나?
            repeatInfoRepository.save(repeatInfo);
        } else {
            repeatInfoRepository.delete(repeatInfo);
            schedule.deleteRepeatInfo();
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
            .repeatInfo(schedule.getRepeatInfo())
            .build();

        scheduleRepository.save(updatedSchedule);
        return ScheduleDto.from(updatedSchedule);
    }

    private RepeatInfo createRepeatInfo(RepeatType repeatType) {
        return RepeatInfo.builder()
            .repeatType(repeatType)
            .repeatStartDate(LocalDate.now().atStartOfDay())
            .repeatEndDate(null)
            .build();
    }

    private List<RepeatDay> createRepeatDays(RepeatInfo repeatInfo, List<RepeatDayType> repeatDays) {
        return repeatDays.stream()
            .map(dayType -> RepeatDay.builder()
                .repeatDayType(dayType)
                .repeatInfo(repeatInfo)
                .build())
            .collect(Collectors.toList());
    }




}
