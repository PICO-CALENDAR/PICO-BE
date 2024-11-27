package com.pico.server.controller.impl;

import com.pico.server.controller.UserApi;
import com.pico.server.dto.ScheduleDto;
import com.pico.server.dto.request.CreateUserDetailsDto;
import com.pico.server.dto.UserInfoDto;
import com.pico.server.dto.request.PartnerUpdateRequest;
import com.pico.server.dto.request.UserInfoUpdateRequest;
import com.pico.server.dto.request.UserRegisterRequest;
import com.pico.server.dto.response.AuthResponse;
import com.pico.server.dto.response.ScheduleResponse;
import com.pico.server.entity.Users;
import com.pico.server.security.dto.response.AuthToken;
import com.pico.server.service.ScheduleService;
import com.pico.server.service.UserDetailsService;
import com.pico.server.service.UserRegisterService;
import com.pico.server.service.UserService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;
    private final UserDetailsService userDetailsService;
    private final UserRegisterService userRegisterService;
    private final ScheduleService scheduleService;

    @Override
    public ResponseEntity<AuthResponse> register(Long userId, UserRegisterRequest request) {
        CreateUserDetailsDto createUserDetailsDto = generateCreateUserDetailsDto(request);
        AuthToken authToken = userRegisterService.register(userId, createUserDetailsDto);
        Users findUser = userService.findById(userId);

        AuthResponse response = AuthResponse.of(findUser,authToken);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<UserInfoDto> getUserInfo(Long userId) {
        UserInfoDto userInfoDto = userService.getUserInfo(userId);
        return ResponseEntity.ok(userInfoDto);
    }

    @Override
    public ResponseEntity<UserInfoDto> updateUserInfo(Long userId, UserInfoUpdateRequest request) {
        UserInfoDto userInfoDto = userDetailsService.updateUserInfo(userId, request.gender(), request.nickName(), request.birth());
        return ResponseEntity.ok(userInfoDto);
    }

    @Override
    public ResponseEntity<UserInfoDto> updatePartnerInfo(Long userId, PartnerUpdateRequest request) {
        UserInfoDto userInfoDto = userDetailsService.updatePartnerInfo(userId, request.partnerId(),request.partnerNickname());
        return ResponseEntity.ok(userInfoDto);
    }

    @Override
    public ResponseEntity<List<ScheduleResponse>> getAllSchedules(Long userId) {
        List<ScheduleResponse> scheduleResponses = new ArrayList<>();
        List<ScheduleDto> schedules = scheduleService.getAllSchedule(userId);
        for(ScheduleDto scheduleDto : schedules) {
            scheduleResponses.add(ScheduleResponse.of(true, scheduleDto));
        }
        return ResponseEntity.ok(scheduleResponses);
    }

    private CreateUserDetailsDto generateCreateUserDetailsDto(UserRegisterRequest request) {
        return CreateUserDetailsDto.builder()
            .gender(request.gender())
            .nickName(request.nickName())
            .birth(request.birth())
            .build();
    }
}
