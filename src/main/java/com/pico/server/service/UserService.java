package com.pico.server.service;

import com.pico.server.dto.CoupleUserDto;
import com.pico.server.dto.request.CreateUserDto;
import com.pico.server.dto.UserInfoDto;
import com.pico.server.entity.UserDetails;
import com.pico.server.entity.Users;
import com.pico.server.exception.ErrorCode;
import com.pico.server.exception.UserException;
import com.pico.server.repository.UserRepository;
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
    public UserInfoDto deleteUser(Long userId) {
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));

        Users partner = userRepository.findById(user.getUserDetails().getPartnerId())
            .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));
        partner.getUserDetails().deleteCoupleInfo();
        userRepository.save(partner);

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
