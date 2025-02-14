package com.prography.ping_pong.dto.response.user;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record FakerResponse(
        @NotBlank String status,
        long code,
        long seed,
        long total,
        List<FakerUserResponse> data
) {

}
