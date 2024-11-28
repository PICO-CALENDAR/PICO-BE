package com.pico.server.controller.impl;

import com.pico.server.controller.ScheduleApi;
import com.pico.server.dto.ScheduleDto;
import com.pico.server.dto.request.CreateScheduleDto;
import com.pico.server.dto.request.DeleteRepeatRequest;
import com.pico.server.dto.request.UpdateScheduleDto;
import com.pico.server.dto.response.ScheduleResponse;
import com.pico.server.entity.Schedule;
import com.pico.server.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ScheduleController implements ScheduleApi {

    private final ScheduleService scheduleService;

    @Override
    public ResponseEntity<ScheduleResponse> add(Long userId, CreateScheduleDto createScheduleDto) {
        ScheduleDto scheduleDto = scheduleService.createSchedule(userId, createScheduleDto);
        ScheduleResponse response = ScheduleResponse.of(true, scheduleDto);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ScheduleResponse> delete(Long userId, Long scheduleId) {
        ScheduleDto scheduleDto = scheduleService.deleteSchedule(userId,scheduleId);
        ScheduleResponse response = ScheduleResponse.of(true, scheduleDto);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ScheduleResponse> deleteRepeatSchedule(Long userId, Long scheduleId, DeleteRepeatRequest deleteRepeatRequest) {
        ScheduleDto scheduleDto = scheduleService.deleteRepeatSchedule(userId, scheduleId, deleteRepeatRequest.repeatEndDate());
        ScheduleResponse response = ScheduleResponse.of(true, scheduleDto);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ScheduleResponse> update(Long userId, Long scheduleId, UpdateScheduleDto updateScheduleDto) {
        ScheduleDto scheduleDto = scheduleService.updateSchedule(userId,scheduleId, updateScheduleDto);
        ScheduleResponse response = ScheduleResponse.of(true, scheduleDto);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ScheduleResponse> getOneSchedule(Long userId, Long scheduleId) {
        ScheduleDto scheduleDto = scheduleService.getOneSchedule(userId, scheduleId);
        ScheduleResponse response = ScheduleResponse.of(true, scheduleDto);
        return ResponseEntity.ok(response);
    }


}
