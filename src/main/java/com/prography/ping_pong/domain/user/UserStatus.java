package com.prography.ping_pong.domain.user;

public enum UserStatus {

    WAIT,
    ACTIVE,
    NON_ACTIVE,
    ;

    public boolean isActive() {
        return this == ACTIVE;
    }
}
