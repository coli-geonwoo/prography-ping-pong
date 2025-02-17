package com.prography.pingpong.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserInitializeRequest(

        @Schema(description = "유저 초기화 시드값", example = "100")
        int seed,

        @Schema(description = "초기화할 유저량", example = "10")
        int quantity
) {

}
