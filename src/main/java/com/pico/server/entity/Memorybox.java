package com.pico.server.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
public class Memorybox extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memorybox_id")
    private Long id;

    private LocalDateTime scheduleStartTime;
    private LocalDateTime scheduleEndTime;
    private LocalDateTime opendate;

    @OneToMany(mappedBy = "memorybox", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Letter> letters = new ArrayList<>();
    @OneToMany(mappedBy = "memorybox", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Photo> photos = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    Users user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anniversary_id", nullable = false)
    Anniversary anniversary;

    public void addLetters(Letter letter) {
        this.letters.add(letter);
    }

    public void addPhotos(Photo photo) {
        this.photos.add(photo);
    }
}
