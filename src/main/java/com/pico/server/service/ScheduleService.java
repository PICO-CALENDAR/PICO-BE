package com.pico.server.service;

import com.pico.server.dto.ScheduleDto;
import com.pico.server.dto.request.CreateScheduleDto;
import com.pico.server.dto.request.UpdateScheduleDto;
import com.pico.server.entity.RepeatInfo;
import com.pico.server.entity.Schedule;
import com.pico.server.entity.Users;
import com.pico.server.enums.RepeatDayType;
import com.pico.server.enums.RepeatType;
import com.pico.server.exception.ErrorCode;
import com.pico.server.exception.ScheduleException;
import com.pico.server.repository.RepeatInfoRepository;
import com.pico.server.repository.ScheduleRepository;
import com.pico.server.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new ScheduleException(ErrorCode.NOT_FOUND_USER));

        RepeatInfo repeatInfo = null;
        if (createScheduleDto.isRepeat()) {
            repeatInfo = createRepeatInfo(createScheduleDto.startTime(), createScheduleDto.repeat());
            if (createScheduleDto.repeat().needsRepeatDay()) {
                List<RepeatDay> repeatDays = createRepeatDays(repeatInfo, createScheduleDto.repeatDays());
                repeatInfo.updateRepeatDays(repeatDays);
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
    public ScheduleDto deleteSchedule(Long userId, Long scheduleId) {
        Schedule schedule = scheduleRepository.findByUserIdAndScheduleId(userId, scheduleId)
            .orElseThrow(() -> new ScheduleException(ErrorCode.NOT_FOUND_SCHEDULE));
        scheduleRepository.delete(schedule);
        return ScheduleDto.from(schedule);
    }

    @Transactional
    public ScheduleDto updateSchedule(Long userId, Long scheduleId, UpdateScheduleDto updateDto) {
        Schedule schedule = scheduleRepository.findByUserIdAndScheduleId(userId, scheduleId)
            .orElseThrow(() -> new ScheduleException(ErrorCode.NOT_FOUND_SCHEDULE));

        RepeatInfo repeatInfo = schedule.getRepeatInfo();
        //repeat Info 3개 update 로직
        //=> isRepeat x면 repeatInfo 삭제, o면 3개 update 로직
        //Schedule Update Logic => 그대로 하되, 바뀐 repeatInfo만 update
        //(repeatinfo에도 schedule 설정? / info랑 schedule 둘다 저장)


        if (Boolean.TRUE.equals(updateDto.isRepeat())) {
            if(updateDto.repeat().needsRepeatDay()) {
                List<RepeatDay> repeatDays = createRepeatDays(repeatInfo, updateDto.repeatDays());
                repeatInfo.updateRepeatInfo(updateDto.repeat(),updateDto.startTime(), repeatDays);
                repeatInfoRepository.save(repeatInfo);
            } else {
                repeatDayRepository.deleteByRepeatInfo(repeatInfo);
                repeatInfo.getRepeatDays().clear();
                repeatInfoRepository.save(repeatInfo);
            }
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

    private RepeatInfo createRepeatInfo(LocalDateTime startTime, RepeatType repeatType) {
        return RepeatInfo.builder()
            .repeatType(repeatType)
            .repeatStartDate(startTime)
            .repeatEndDate(null)
            .build();
    }

    private List<RepeatDay> createRepeatDays(RepeatInfo repeatInfo, List<RepeatDayType> repeatDayTypes) {
        return repeatDayTypes.stream()
            .map(dayType -> RepeatDay.builder()
                .repeatDayType(dayType)
                .repeatInfo(repeatInfo)
                .build())
            .collect(Collectors.toList());
    }




}
