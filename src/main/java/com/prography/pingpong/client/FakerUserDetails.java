package com.prography.pingpong.client;

import jakarta.validation.constraints.NotNull;
import java.util.Comparator;
import java.util.List;
import lombok.Getter;

@Getter
public class FakerUserDetails {

    private static final Comparator<FakerUserDetail> USER_DETAIL_COMPARATOR = Comparator.comparing(FakerUserDetail::getFakerId);

    @NotNull
    private final List<FakerUserDetail> fakerUserDetails;

    public FakerUserDetails(List<FakerUserDetail> fakerUserDetails) {
        fakerUserDetails.sort(USER_DETAIL_COMPARATOR);
        this.fakerUserDetails = fakerUserDetails;
    }
}
