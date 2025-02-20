package com.prography.pingpong.domain.room;

import com.prography.pingpong.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class Room extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String title;

    @NotNull
    @Column(name = "member_id")
    private long hostId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    public Room(String title, long hostId, RoomType roomType) {
        this(null, title, hostId, roomType, RoomStatus.WAIT);
    }

    public boolean isWait() {
        return status.isWait();
    }

    public boolean canEnter(long participantCount) {
        return isWait() && !roomType.isFull(participantCount);
    }

    public boolean isHost(long userId) {
        return hostId == userId;
    }

    public void start() {
        this.status = RoomStatus.PROGRESS;
    }

    public void finished() {
        this.status = RoomStatus.FINISH;
    }
}
