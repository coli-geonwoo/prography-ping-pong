package com.prography.pingpong.domain.userroom;

public enum Team {

    BLUE,
    RED,
    ;

    public Team opposite() {
        if (this == BLUE) {
            return RED;
        }
        return BLUE;
    }
}
