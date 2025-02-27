package com.pico.server.service;

import com.pico.server.dto.CoupleUserDto;
import com.pico.server.entity.Users;
import com.pico.server.exception.ErrorCode;
import com.pico.server.exception.InviteCodeException;
import com.pico.server.exception.UserException;
import com.pico.server.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

@Service
@RequiredArgsConstructor
public class InviteCodeService {

    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final String INVITE_CODE_KEY = "inviteCode:";

    @Transactional
    public String makeInviteCode(Long userId) {
        validateCouple(userId);
        String inviteCode = NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR,NanoIdUtils.DEFAULT_ALPHABET, 10);
        String key = INVITE_CODE_KEY + inviteCode;

        Set<String> keys = redisTemplate.keys("inviteCode:*");
        if (keys != null) {
            for (String existingKey : keys) {
                String value = redisTemplate.opsForValue().get(existingKey);
                if (String.valueOf(userId).equals(value)) {
                    redisTemplate.delete(existingKey);
                    break;
                }
            }
        }
        redisTemplate.opsForValue().set(key, userId.toString());

        return inviteCode;
    }

    @Transactional
    public List<CoupleUserDto> makeCouple(Long userId, String inviteCode) {
        validateInviteCode(inviteCode);
        validateCouple(userId);

        String key = INVITE_CODE_KEY + inviteCode;
        Long partnerUserId = Long.parseLong(
            Objects.requireNonNull(redisTemplate.opsForValue().get(key)));

        List<CoupleUserDto> users = new ArrayList<>();
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));

        Users partner = userRepository.findById(partnerUserId)
            .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));

        user.getUserDetails().registerPartnerInfo(partner.getId(), partner.getUserDetails().getNickName(), partner.getProfileImage(), partner.getUserDetails().getName());
        partner.getUserDetails().registerPartnerInfo(userId, user.getUserDetails().getNickName(), user.getProfileImage(), user.getUserDetails().getName());
        userRepository.save(user);
        userRepository.save(partner);

        users.add(CoupleUserDto.of(user));
        users.add(CoupleUserDto.of(partner));
        return users;
    }


    private Users validateCouple(Long userId) {
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));

        if(user.getUserDetails().getPartnerId() != null) {
            throw new InviteCodeException(ErrorCode.ALREADY_PARTNER_EXISTS);
        }
        return user;
    }

    private void validateInviteCode(String inviteCode) {
        String key = INVITE_CODE_KEY + inviteCode;
        if(Boolean.FALSE.equals(redisTemplate.hasKey(key))) {
            throw new InviteCodeException(ErrorCode.NOT_FOUND_INVITE_CODE);
        }
    }
}
