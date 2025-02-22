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
import com.pico.server.repository.MemoryboxRepository;
import com.pico.server.repository.ScheduleRepository;
import com.pico.server.repository.UserRepository;
import com.pico.server.security.enums.Platform;
import com.pico.server.service.apple.AppleApiClient;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final AppleRefreshTokenRepository appleRefreshTokenRepository;
    private final MemoryboxRepository memoryboxRepository;
    private final AppleApiClient appleApiClient;
    private final S3Service s3Service;

    private final String USER_PROFILE_PREFIX = "USER_";

    @Transactional
    public Users saveUser(CreateUserDto createUserDto) {

        Optional<Users> existUser = userRepository.findByPlatformAndPlatformId(
            createUserDto.platform(),
            createUserDto.platformId());

        if (existUser.isPresent()) {
            return updateProfileOfExistUser(createUserDto, existUser.get());
        }

        Users newUser = Users.builder()
            .email(createUserDto.email())
            .profileImage(createUserDto.profileImage())
            .platform(createUserDto.platform())
            .platformId(createUserDto.platformId())
            .isRegistered(false)
            .build();

        return userRepository.save(newUser);
    }

    @Transactional
    @Valid
    public Users saveAppleUser(CreateUserDto createUserDto) {

        Optional<Users> existUser = userRepository.findByPlatformAndEmail(
            createUserDto.platform(),
            createUserDto.email());

        if (existUser.isPresent()) {
            return updateProfileOfExistUser(createUserDto, existUser.get());
        }

        Users newUser = Users.builder()
            .email(createUserDto.email())
            .profileImage(null)
            .platform(Platform.APPLE)
            .platformId(null)
            .isRegistered(false)
            .build();

        return userRepository.save(newUser);
    }

    @Transactional
    public UserInfoDto updateUserProfile(Long userId, MultipartFile uploadFile) throws IOException {
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));

        String fileName = USER_PROFILE_PREFIX + userId.toString();
        if(user.getProfileImage().contains(USER_PROFILE_PREFIX)) {
            s3Service.deleteUserMultipartImage(fileName);
        }
        String putImageUrl = s3Service.putUserMultipartImage(uploadFile, fileName);

        user.updateProfileImage(putImageUrl);
        userRepository.save(user);
        return UserInfoDto.of(user, user.getUserDetails());
    }

    @Transactional
    public UserInfoDto deleteUser(Long userId) {
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));

        if(user.getPlatform() == Platform.APPLE) {
            revokeAppleToken(userId);
            appleRefreshTokenRepository.deleteAppleRefreshTokenByUserId(userId);
        }

        if(user.getUserDetails().getPartnerId() != null) {
            Users partner = userRepository.findById(user.getUserDetails().getPartnerId())
                .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));
            partner.getUserDetails().deleteCoupleInfo();
            userRepository.save(partner);
        }
        scheduleRepository.deleteAllByUser(user);
        userRepository.delete(user);
        memoryboxRepository.deleteByUserId(userId);
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
    public Boolean revokeOnlyAppleToken(String refreshToken) {
        try {
            appleApiClient.revokeToken(refreshToken);
            return true;
        } catch (Exception e) {
            throw new UserException(ErrorCode.FAIL_TO_DELETE_APPLE_USER);
        }
    }

    @Transactional
    public Boolean revokeAppleTokenByAuthCode(String authorizationCode) {
        String refreshToken = appleApiClient.getAppleRefreshToken(authorizationCode);
        System.out.println(refreshToken);
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
        existUser.updateEmail(createUserDto.email());
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
