package com.prography.pingpong.dto.request.room;

import com.prography.pingpong.domain.room.Room;
import com.prography.pingpong.domain.room.RoomType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record RoomCreateRequest(
        @Schema(description = "유저 아이디", example = "1")
        long userId,

        @NotNull
        @Schema(description = "방 타입(단식-복식)", example = "SINGLE")
        RoomType roomType,

        @NotNull
        @Schema(description = "방 제목", example = "복식하실 분")
        String title
) {

    public Room toRoom() {
        return new Room(title, userId, roomType);
    }
}
