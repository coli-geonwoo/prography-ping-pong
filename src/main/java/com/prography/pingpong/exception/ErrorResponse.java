package com.prography.pingpong.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ErrorResponse(
        @NotNull
        @Schema(description = "에러 코드", example = "201")
        long code,

        @NotBlank
        @Schema(description = "에러 메시지", example = "불가능한 요청입니다")
        String message
) {

}
