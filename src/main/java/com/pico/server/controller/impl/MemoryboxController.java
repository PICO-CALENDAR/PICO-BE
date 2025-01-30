package com.pico.server.controller.impl;

import com.pico.server.controller.MemoryboxApi;
import com.pico.server.dto.LetterDto;
import com.pico.server.dto.MemoryboxDto;
import com.pico.server.dto.PhotoDto;
import com.pico.server.dto.request.MemoryboxRequest;
import com.pico.server.dto.request.MemoryboxUpdateRequest;
import com.pico.server.dto.response.AnniversaryResponse;
import com.pico.server.dto.response.ListResponse;
import com.pico.server.dto.response.MemoryboxResponse;
import com.pico.server.entity.Anniversary;
import com.pico.server.entity.Users;
import com.pico.server.service.AnniversaryService;
import com.pico.server.service.MemoryboxService;
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

    private final AnniversaryService anniversaryService;
    private final MemoryboxService memoryboxService;
    private final UserService userService;

    @Override
    public ResponseEntity<ListResponse<AnniversaryResponse>> getThreeMonthsAnniversaries() {
        List<AnniversaryResponse> anniverssaryList = anniversaryService.findThreeMonthsAnniversaries();
        ListResponse<AnniversaryResponse> response =ListResponse.from(anniverssaryList);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<MemoryboxResponse> saveMemorybox(Long userId, Long anniversaryId, MemoryboxRequest request)
        throws IOException {
        Anniversary anniversary = anniversaryService.findById(anniversaryId);
        Users user = userService.findById(userId);
        MemoryboxDto memoryBox = memoryboxService.saveMemoryBox(user,anniversary, request);

        List<LetterDto> letters = LetterDto.from(memoryBox.letters());
        List<PhotoDto> photos = PhotoDto.from(memoryBox.photos());
        MemoryboxResponse response = MemoryboxResponse.of(memoryBox, letters, photos);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<MemoryboxResponse> addPartnerMemory(Long userId, Long memoryboxId, MemoryboxPartnerRequest request)
        throws IOException {
        MemoryboxDto memoryBox = memoryboxService.addPartnerMemory(userId, memoryboxId, request);

        List<LetterDto> letters = LetterDto.from(memoryBox.letters());
        List<PhotoDto> photos = PhotoDto.from(memoryBox.photos());
        MemoryboxResponse response = MemoryboxResponse.of(memoryBox, letters, photos);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<MemoryboxResponse> updateMemorybox(Long userId, Long memoryboxId,
        MemoryboxUpdateRequest request) throws IOException {
        MemoryboxDto memoryBox = memoryboxService.updateMemorybox(userId, memoryboxId, request);

        List<LetterDto> letters = LetterDto.from(memoryBox.letters());
        List<PhotoDto> photos = PhotoDto.from(memoryBox.photos());
        MemoryboxResponse response = MemoryboxResponse.of(memoryBox, letters, photos);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Boolean> deleteMemorybox(Long userId, Long memoryboxId) {
        memoryboxService.deleteMemorybox(memoryboxId);
        return ResponseEntity.ok(true);
    }

    @Override
    public ResponseEntity<Boolean> deleteCoupleMemorybox(Long userId) {
        memoryboxService.deleteAllMemoryBox(userId);
        return ResponseEntity.ok(true);
    }

    @Override
    public ResponseEntity<ListResponse<MemoryboxResponse>> getAnniversaryMemoryboxes(Long userId, Long anniveraryId) {
        List<MemoryboxResponse> memoryBoxes = memoryboxService.getAnniversaryMemoryboxes(userId, anniveraryId);
        ListResponse<MemoryboxResponse> response = ListResponse.from(memoryBoxes);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ListResponse<MemoryboxResponse>> getAllMemoryboxes(Long userId) {
        List<MemoryboxResponse> memoryBoxes = memoryboxService.getAllMemoryboxes(userId);
        ListResponse<MemoryboxResponse> response = ListResponse.from(memoryBoxes);
        return ResponseEntity.ok(response);
    }
}
