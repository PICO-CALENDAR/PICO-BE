package com.pico.server.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pico.server.enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_details_id")
    private Long id;

    private Long partnerId;
    private String partnerName;

    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String nickName;
    @Past
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
    private LocalDate birth;

    public void updatePartnerInfo(Long partnerId, String partnerName) {
        this.partnerId = partnerId;
        this.partnerName = partnerName;
    }

    public void updateUserInfo(Gender gender, String nickName, LocalDate birth) {
        this.gender = gender == null ? this.gender : gender;
        this.nickName = nickName == null ? this.nickName : nickName;
        this.birth = birth == null ? this.birth : birth;
    }
}
