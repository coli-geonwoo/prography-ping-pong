package com.prography.pingpong.domain.user;

public enum UserStatus {

    WAIT,
    ACTIVE,
    NON_ACTIVE,
    ;

    public boolean isActive() {
        return this == ACTIVE;
    }
}
