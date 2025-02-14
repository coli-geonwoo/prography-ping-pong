package com.prography.ping_pong.service.user.client;

import com.prography.ping_pong.domain.user.UserStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class UserDetail {

    private static final long USER_ACTIVE_THRESHOLD = 30;
    private static final long USER_WAIT_THRESHOLD = 60;

    private final long fakerId;

    @NotBlank
    private final String name;

    @NotBlank
    private final UserStatus status;

    @NotBlank
    private final String email;


    public UserDetail(long fakerId, String name, String email) {
        this.fakerId = fakerId;
        this.name = name;
        this.status = mapStatus(fakerId);
        this.email = email;
    }

    private UserStatus mapStatus(long fakerId) {
        if (fakerId <= USER_ACTIVE_THRESHOLD) {
            return UserStatus.ACTIVE;
        }

        if (USER_ACTIVE_THRESHOLD < fakerId && fakerId <= USER_WAIT_THRESHOLD) {
            return UserStatus.WAIT;
        }

        return UserStatus.NON_ACTIVE;
    }
}
