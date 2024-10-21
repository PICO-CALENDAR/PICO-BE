package com.pico.server.service;

import com.pico.server.dto.CreateUserDetailsDto;
import com.pico.server.dto.UserInfoDto;
import com.pico.server.entity.UserDetails;
import com.pico.server.entity.Users;
import com.pico.server.enums.Gender;
import com.pico.server.repository.UserDetailsRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserDetailsService {

    private final UserService userService;
    private final UserDetailsRepository userDetailsRepository;

    @Transactional
    public UserDetails saveUserDetails(CreateUserDetailsDto createUserDetailsDto) {

        UserDetails userDetails = UserDetails.builder()
            .gender(createUserDetailsDto.gender())
            .nickName(createUserDetailsDto.nickName())
            .birth(createUserDetailsDto.birth())
            .build();

        return userDetailsRepository.save(userDetails);
    }

    @Transactional
    public UserInfoDto updateUserInfo(Long userId, Gender gender, String nickName, LocalDate birth) {
        Users findUser = userService.findById(userId);
        UserDetails userDetails = findUser.getUserDetails();
        userDetails.updateUserInfo(gender,nickName,birth);

        userDetailsRepository.save(userDetails);
        return UserInfoDto.of(findUser, userDetails);
    }

    @Transactional
    public UserInfoDto updatePartnerInfo(Long userId, Long partnerId, String parterName) {
        Users findUser = userService.findById(userId);
        UserDetails userDetails = findUser.getUserDetails();
        userDetails.updatePartnerInfo(partnerId, parterName);

        userDetailsRepository.save(userDetails);
        return UserInfoDto.of(findUser, userDetails);
    }
}

