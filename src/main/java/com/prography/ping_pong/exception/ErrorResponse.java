package com.prography.ping_pong.exception;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ErrorResponse(
        @NotNull long code,
        @NotBlank String message
) {

}
