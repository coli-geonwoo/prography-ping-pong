package com.prography.pingpong.dto.response.room;

import com.prography.pingpong.domain.room.Room;
import com.prography.pingpong.domain.room.RoomStatus;
import com.prography.pingpong.domain.room.RoomType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RoomCreateResponse(
        @Schema(description = "방 아이디", example = "1")
        long id,

        @NotBlank
        @Schema(description = "방 제목", example = "단식하실 분")
        String title,

        @Schema(description = "방장 아이디", example = "1")
        long userId,

        @NotNull
        @Schema(description = "방의 타입(단식-복식)", example = "SINGLE")
        RoomType roomType,

        @NotNull
        @Schema(description = "방의 상태", example = "WAIT")
        RoomStatus status
) {

    public RoomCreateResponse(Room room) {
        this(room.getId(), room.getTitle(), room.getHostId(), room.getRoomType(), room.getStatus());
    }
}
