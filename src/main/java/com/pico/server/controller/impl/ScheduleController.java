package com.pico.server.controller.impl;

import com.pico.server.controller.ScheduleApi;
import com.pico.server.dto.ScheduleDto;
import com.pico.server.dto.request.CreateScheduleDto;
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


}
