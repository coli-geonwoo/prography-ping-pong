package com.prography.pingpong.dto.response.room;

import com.prography.pingpong.domain.room.Room;
import com.prography.pingpong.domain.room.RoomStatus;
import com.prography.pingpong.domain.room.RoomType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RoomCreateResponse(
        long id,
        @NotBlank String title,
        long userId,
        @NotNull RoomType roomType,
        @NotNull RoomStatus status
) {

    public RoomCreateResponse(Room room) {
        this(room.getId(), room.getTitle(), room.getHostId(), room.getRoomType(), room.getStatus());
    }
}
