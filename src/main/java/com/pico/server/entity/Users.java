package com.pico.server.entity;

import com.pico.server.security.enums.Platform;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
public class Users extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_details_id")
    private UserDetails userDetails;


    private String name;
    private String email;
    private String profileImage;
    @Enumerated(EnumType.STRING)
    private Platform platform;
    private String platformId;
    private Boolean isRegistered;

    public void updateProfile(String email, String name, String profileImage) {
        this.email = email;
        this.name = name;
        this.profileImage = profileImage;
    }

    public void register(UserDetails userDetails) {
        this.isRegistered = true;
        this.userDetails = userDetails;
    }
}
