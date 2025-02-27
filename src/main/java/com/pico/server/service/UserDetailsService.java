package com.pico.server.service;

import com.pico.server.dto.request.CreateUserDetailsDto;
import com.pico.server.dto.UserInfoDto;
import com.pico.server.dto.request.UserInfoUpdateRequest;
import com.pico.server.entity.UserDetails;
import com.pico.server.entity.Users;
import com.pico.server.enums.Gender;
import com.pico.server.exception.ErrorCode;
import com.pico.server.exception.UserException;
import com.pico.server.repository.UserDetailsRepository;
import com.pico.server.repository.UserRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final UserDetailsRepository userDetailsRepository;

    @Transactional
    public UserDetails saveUserDetails(CreateUserDetailsDto createUserDetailsDto) {

        UserDetails userDetails = UserDetails.builder()
            .name(createUserDetailsDto.name())
            .gender(createUserDetailsDto.gender())
            .nickName(createUserDetailsDto.nickName())
            .birth(createUserDetailsDto.birth())
            .dday(createUserDetailsDto.dday())
            .isTermsAgreed(createUserDetailsDto.isTermsAgreed())
            .isMarketingAgreed(createUserDetailsDto.isMarketingAgreed())
            .build();

        return userDetailsRepository.save(userDetails);
    }

    @Transactional
    public UserInfoDto updateUserInfo(Long userId, UserInfoUpdateRequest request) {
        Users findUser = userService.findById(userId);
        UserDetails userDetails = findUser.getUserDetails();
        userDetails.updateUserInfo(request.name(), request.gender(),request.nickName(),request.birth(),request.dday(), request.isTermsAgreed(), request.isMarketingAgreed());

        if(userDetails.getPartnerId() != null) {
            UserDetails partnerUserInfo = userRepository.findPartnerByUserId(userId).getUserDetails();
            partnerUserInfo.updatePartnerNames(request.nickName(), request.name());
            userDetailsRepository.save(partnerUserInfo);
        }

        userDetailsRepository.save(userDetails);
        return UserInfoDto.of(findUser, userDetails);
    }
}

