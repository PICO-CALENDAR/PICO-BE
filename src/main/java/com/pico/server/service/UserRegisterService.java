package com.pico.server.service;

import com.pico.server.dto.CreateUserDetailsDto;
import com.pico.server.entity.UserDetails;
import com.pico.server.entity.Users;
import com.pico.server.security.dto.response.AuthToken;
import com.pico.server.security.util.AuthTokenGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserRegisterService {

    private final UserService userService;
    private final UserDetailsService userDetailsService;
    private final AuthTokenGenerator authTokenGenerator;

    @Transactional
    public AuthToken register(Long userId, CreateUserDetailsDto createUserDetailsDto) {
        Users findUser = userService.findById(userId);
        UserDetails userDetails = userDetailsService.saveUserDetails(createUserDetailsDto);
        findUser.register(userDetails);

        return authTokenGenerator.generateAuthToken(findUser);
    }
}
