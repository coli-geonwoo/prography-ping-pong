package com.prography.pingpong.client;

import com.prography.pingpong.domain.user.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class FakerUserDetail {

    private static final long USER_ACTIVE_THRESHOLD = 30;
    private static final long USER_WAIT_THRESHOLD = 60;

    private final long fakerId;

    @NotBlank
    private final String name;

    @NotBlank
    private final UserStatus status;

    @Email
    @NotBlank
    private final String email;


    public FakerUserDetail(long fakerId, String name, String email) {
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
