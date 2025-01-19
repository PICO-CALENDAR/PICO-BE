package com.pico.server.service;

import com.pico.server.dto.CoupleUserDto;
import com.pico.server.dto.request.CreateUserDto;
import com.pico.server.dto.UserInfoDto;
import com.pico.server.entity.AppleRefreshToken;
import com.pico.server.entity.UserDetails;
import com.pico.server.entity.Users;
import com.pico.server.exception.AuthException;
import com.pico.server.exception.ErrorCode;
import com.pico.server.exception.UserException;
import com.pico.server.repository.AppleRefreshTokenRepository;
import com.pico.server.repository.ScheduleRepository;
import com.pico.server.repository.UserRepository;
import com.pico.server.security.enums.Platform;
import com.pico.server.service.apple.AppleApiClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final AppleRefreshTokenRepository appleRefreshTokenRepository;
    private final AppleApiClient appleApiClient;

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

    @Transactional
    public Users saveAppleUser(CreateUserDto createUserDto) {

        Optional<Users> existUser = userRepository.findByPlatformAndEmail(
            createUserDto.platform(),
            createUserDto.email());

        if (existUser.isPresent()) {
            return updateProfileOfExistUser(createUserDto, existUser.get());
        }

        Users newUser = Users.builder()
            .name(createUserDto.name())
            .email(createUserDto.email())
            .platform(Platform.APPLE)
            .isRegistered(false)
            .build();


        return userRepository.save(newUser);
    }

    @Transactional
    public UserInfoDto deleteUser(Long userId) {
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));

        if(user.getPlatform() == Platform.APPLE) {
            revokeAppleToken(userId);
        }

        if(user.getUserDetails().getPartnerId() != null) {
            Users partner = userRepository.findById(user.getUserDetails().getPartnerId())
                .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));
            partner.getUserDetails().deleteCoupleInfo();
            userRepository.save(partner);
        }
        scheduleRepository.deleteAllByUser(user);
        userRepository.delete(user);
        return UserInfoDto.of(user, user.getUserDetails());
    }

    @Transactional
    public List<CoupleUserDto> deleteCouple(Long userId) {
        List<CoupleUserDto> users = new ArrayList<>();
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));

        Users partner = userRepository.findById(user.getUserDetails().getPartnerId())
            .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));

        user.getUserDetails().deleteCoupleInfo();
        partner.getUserDetails().deleteCoupleInfo();
        userRepository.save(user);
        userRepository.save(partner);

        users.add(CoupleUserDto.of(user));
        users.add(CoupleUserDto.of(partner));
        return users;
    }

    @Transactional
    public Boolean revokeOnlyAppleToken(String authorizationCode) {
        String refreshToken = appleApiClient.getAppleRefreshToken(authorizationCode);
        try {
            appleApiClient.revokeToken(refreshToken);
            return true;
        } catch (Exception e) {
            throw new UserException(ErrorCode.FAIL_TO_DELETE_APPLE_USER);
        }
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
        existUser.updateNameAndEmail(createUserDto.email(), createUserDto.name());
        userRepository.save(existUser);
        return existUser;
    }

    private void revokeAppleToken(Long userId) {
        try {
            AppleRefreshToken appleRefreshToken = appleRefreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new AuthException(ErrorCode.NOT_FOUND_APPLE_REFRESH_TOKEN));
            appleApiClient.revokeToken(appleRefreshToken.getRefreshToken());
            appleRefreshTokenRepository.delete(appleRefreshToken);
        } catch (Exception e) {
            throw new UserException(ErrorCode.FAIL_TO_DELETE_APPLE_USER);
        }
    }

}
