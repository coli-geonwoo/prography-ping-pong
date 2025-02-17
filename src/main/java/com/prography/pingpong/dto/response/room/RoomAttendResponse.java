package com.prography.pingpong.dto.response.room;

import io.swagger.v3.oas.annotations.media.Schema;

public record RoomAttendResponse(

        @Schema(description = "방에 참여한 유저 id", example = "1")
        long userId,

        @Schema(description = "참여한 방 id", example = "1")
        long roomId
) {

}
