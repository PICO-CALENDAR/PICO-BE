package com.pico.server.service;

import com.pico.server.dto.CreateUserDto;
import com.pico.server.dto.UserInfoDto;
import com.pico.server.entity.UserDetails;
import com.pico.server.entity.Users;
import com.pico.server.exception.ErrorCode;
import com.pico.server.exception.UserException;
import com.pico.server.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public Users saveUser(CreateUserDto createUserDto) {

        Optional<Users> existUser = userRepository.findByPlatformAndPlatformId(
            createUserDto.platform(),
            createUserDto.platformId());

        if (existUser.isPresent()) {
            return updateProfileOfExistUser(createUserDto, existUser.get());
        }

        Users newUser = Users.builder()
            .name(createUserDto.name())
            .email(createUserDto.email())
            .profileImage(createUserDto.profileImage())
            .platform(createUserDto.platform())
            .platformId(createUserDto.platformId())
            .isRegistered(false)
            .build();

        return userRepository.save(newUser);
    }

    public Users findById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));
    }

    public UserInfoDto getUserInfo(Long userId) {
        Users findUser = findById(userId);
        UserDetails userDetails = findUser.getUserDetails();

        return UserInfoDto.of(findUser, userDetails);
    }

    private Users updateProfileOfExistUser(CreateUserDto createUserDto, Users existUser) {
        existUser.updateProfile(createUserDto.email(), createUserDto.name(), createUserDto.profileImage());
        userRepository.save(existUser);
        return existUser;
    }

}
