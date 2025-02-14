package com.prography.ping_pong.service.user.client;

import jakarta.validation.constraints.NotNull;
import java.util.Comparator;
import java.util.List;
import lombok.Getter;

@Getter
public class UserDetails {

    private static final Comparator<UserDetail> USER_DETAIL_COMPARATOR = Comparator.comparing(UserDetail::getFakerId);

    @NotNull
    private final List<UserDetail> userDetails;

    public UserDetails(List<UserDetail> userDetails) {
        userDetails.sort(USER_DETAIL_COMPARATOR);
        this.userDetails = userDetails;
    }
}
