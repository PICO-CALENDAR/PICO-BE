package com.pico.server.controller.impl;

import com.pico.server.controller.MemoryboxApi;
import com.pico.server.dto.LetterDto;
import com.pico.server.dto.MemoryUserDto;
import com.pico.server.dto.MemoryboxDto;
import com.pico.server.dto.PhotoDto;
import com.pico.server.dto.ScheduleDto;
import com.pico.server.dto.request.MemoryboxRequest;
import com.pico.server.dto.request.MemoryboxUpdateRequest;
import com.pico.server.dto.response.ListResponse;
import com.pico.server.dto.response.MemoryboxResponse;
import com.pico.server.dto.response.MemoryboxScheduleResponse;
import com.pico.server.entity.Users;
import com.pico.server.service.MemoryboxService;
import com.pico.server.service.ScheduleService;
import com.pico.server.service.UserService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MemoryboxController implements MemoryboxApi {

    private final MemoryboxService memoryboxService;
    private final ScheduleService scheduleService;
    private final UserService userService;

    @Override
    public ResponseEntity<ListResponse<ScheduleDto>> getThreeMonthsAnniversaries(Long userId) {
        List<ScheduleDto> schedulDtos =  scheduleService.getThreeMonthsAnniversarySchedules(userId);
        ListResponse<ScheduleDto> response =ListResponse.from(schedulDtos);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<MemoryboxResponse> saveMemorybox(Long userId, MemoryboxRequest request)
        throws IOException {
        Users user = userService.findById(userId);
        MemoryboxDto memoryBox = memoryboxService.saveMemoryBox(user, request);
        MemoryUserDto author = memoryboxService.findMemoryboxUser(memoryBox.memoryboxId());

        List<LetterDto> letters = LetterDto.from(memoryBox.letters());
        List<PhotoDto> photos = PhotoDto.from(memoryBox.photos());
        MemoryboxResponse response = MemoryboxResponse.of(memoryBox, letters, photos,author);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<MemoryboxResponse> updateMemorybox(Long userId, Long memoryboxId,
        MemoryboxUpdateRequest request) throws IOException {
        MemoryboxDto memoryBox = memoryboxService.updateMemorybox(userId, memoryboxId, request);
        MemoryUserDto author = memoryboxService.findMemoryboxUser(memoryBox.memoryboxId());

        List<LetterDto> letters = LetterDto.from(memoryBox.letters());
        List<PhotoDto> photos = PhotoDto.from(memoryBox.photos());
        MemoryboxResponse response = MemoryboxResponse.of(memoryBox, letters, photos,author);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Boolean> deleteMemorybox(Long userId, Long memoryboxId) {
        memoryboxService.deleteMemorybox(memoryboxId);
        return ResponseEntity.ok(true);
    }

    @Override
    public ResponseEntity<Boolean> deleteAllMemoryboxes(Long userId) {
        memoryboxService.deleteAllMemoryBox(userId);
        return ResponseEntity.ok(true);
    }

    @Override
    public ResponseEntity<ListResponse<MemoryboxScheduleResponse>> getAllMemoryboxes(Long userId) {
        List<MemoryboxScheduleResponse> memoryBoxes = memoryboxService.getAllMemoryboxes(userId);
        ListResponse<MemoryboxScheduleResponse> response = ListResponse.from(memoryBoxes);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ListResponse<MemoryboxResponse>> getAnniversaryMemoryboxes(Long userId, Long scheduleId) {
        List<MemoryboxResponse> memoryBoxes = memoryboxService.getAnniversaryMemoryboxes(userId, scheduleId);
        ListResponse<MemoryboxResponse> response = ListResponse.from(memoryBoxes);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ListResponse<MemoryboxResponse>> getMyPastMemoryboxes(Long userId) {
        List<MemoryboxResponse> memoryBoxes = memoryboxService.getMyPastMemoryboxes(userId);
        ListResponse<MemoryboxResponse> response = ListResponse.from(memoryBoxes);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ListResponse<MemoryboxResponse>> getMyUpcomingMemoryboxes(Long userId) {
        List<MemoryboxResponse> memoryBoxes = memoryboxService.getMyUpcomingMemoryboxes(userId);
        ListResponse<MemoryboxResponse> response = ListResponse.from(memoryBoxes);
        return ResponseEntity.ok(response);
    }
}
