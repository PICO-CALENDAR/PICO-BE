package com.pico.server.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pico.server.entity.UserDetails;
import com.pico.server.entity.Users;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.Builder;
import org.springframework.lang.Nullable;

@Builder
public record UserInfoDto(
    String name,
    String profileImage,

    String email,
    String gender,
    String nickName,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
    String birth,

    @Nullable
    Long partnerId,

    @Nullable
    String partnerName
) {

    public static UserInfoDto of(Users user, UserDetails userDetails) {
        return UserInfoDto.builder()
            .name(user.getName())
            .profileImage(user.getProfileImage())
            .email(user.getEmail())
            .gender(userDetails.getGender().getValue())
            .nickName(userDetails.getNickName())
            .birth(userDetails.getBirth().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")))
            .partnerId(userDetails.getPartnerId())
            .partnerName(userDetails.getPartnerName())
            .build();
    }
}
