package com.pico.server.controller.impl;

import com.pico.server.controller.UserApi;
import com.pico.server.dto.request.CreateUserDetailsDto;
import com.pico.server.dto.UserInfoDto;
import com.pico.server.dto.request.MakeCoupleRequest;
import com.pico.server.dto.request.UserInfoUpdateRequest;
import com.pico.server.dto.request.UserRegisterRequest;
import com.pico.server.dto.response.AuthResponse;
import com.pico.server.dto.response.CoupleResponse;
import com.pico.server.dto.CoupleUserDto;
import com.pico.server.dto.response.InviteCodeResponse;
import com.pico.server.entity.Users;
import com.pico.server.exception.ErrorCode;
import com.pico.server.exception.UserException;
import com.pico.server.security.dto.response.AuthToken;
import com.pico.server.service.InviteCodeService;
import com.pico.server.service.ScheduleService;
import com.pico.server.service.UserDetailsService;
import com.pico.server.service.UserRegisterService;
import com.pico.server.service.UserService;
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
    private final InviteCodeService inviteCodeService;

    @Override
    public ResponseEntity<AuthResponse> register(Long userId, UserRegisterRequest request) {
        if(Boolean.FALSE.equals(request.isTermsAgreed())) {
            throw new UserException(ErrorCode.TERMS_NOT_AGREED);
        }
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
        UserInfoDto userInfoDto = userDetailsService.updateUserInfo(userId, request.gender(), request.nickName(), request.birth(), request.dday(), request.isTermsAgreed(), request.isMarketingAgreed());
        return ResponseEntity.ok(userInfoDto);
    }

    @Override
    public ResponseEntity<InviteCodeResponse> makeInviteCode(Long userId) {
        String inviteCode = inviteCodeService.makeInviteCode(userId);
        InviteCodeResponse response = InviteCodeResponse.of(inviteCode);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CoupleResponse<CoupleUserDto>> makeCouple(Long userId, MakeCoupleRequest makeCoupleRequest) {
        List<CoupleUserDto> users = inviteCodeService.makeCouple(userId, makeCoupleRequest.inviteCode());
        return ResponseEntity.ok(CoupleResponse.from(users));
    }

    private CreateUserDetailsDto generateCreateUserDetailsDto(UserRegisterRequest request) {
        return CreateUserDetailsDto.builder()
            .gender(request.gender())
            .nickName(request.nickName())
            .birth(request.birth())
            .dday(request.dday())
            .isTermsAgreed(request.isTermsAgreed())
            .isMarketingAgreed(request.isMarketingAgreed())
            .build();
    }
}
