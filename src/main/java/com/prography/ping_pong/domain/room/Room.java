package com.prography.ping_pong.domain.room;

import com.prography.ping_pong.domain.BaseEntity;
import com.prography.ping_pong.domain.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Room extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String title;

    @NotNull
    @JoinColumn(name = "member_id")
    @OneToOne(fetch = FetchType.LAZY)
    private User host;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    public Room(String title, User host, RoomType roomType) {
        this(null, title, host, roomType, RoomStatus.WAIT);
    }
}
