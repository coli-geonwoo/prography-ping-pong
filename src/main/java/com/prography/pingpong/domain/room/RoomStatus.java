package com.prography.pingpong.domain.room;

public enum RoomStatus {
    WAIT,
    PROGRESS,
    FINISH,
    ;

    public boolean isWait() {
        return this == WAIT;
    }
}
