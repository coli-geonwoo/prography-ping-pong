package com.prography.pingpong.dto.request.room;

import io.swagger.v3.oas.annotations.media.Schema;

public record RoomAttendRequest(

        @Schema(description = "유저 아이디", example = "1")
        long userId
) {

}
