package com.prography.ping_pong.domain.room;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoomType {

    SINGLE(2, 1),
    DOUBLE(4, 2),
    ;

    private final long totalCapacity;
    private final long teamCapacity;

    public boolean isFull(long participantCount) {
        return participantCount >= totalCapacity;
    }

    public boolean isLessThanOneTeamCapacity(long participantCount) {
        return participantCount < teamCapacity;
    }
}
